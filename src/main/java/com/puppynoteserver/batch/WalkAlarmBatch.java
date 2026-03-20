package com.puppynoteserver.batch;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.expo.event.PushNotificationEvent;
import com.puppynoteserver.expo.request.SendPushDataDto;
import com.puppynoteserver.expo.request.SendPushServiceRequest;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalkAlarmBatch {

    private final PetWalkAlarmRepository petWalkAlarmRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PushRepository pushRepository;
    private final AlertSettingReadService alertSettingReadService;
    private final ApplicationEventPublisher eventPublisher;

    // 5분마다 실행, 10분 뒤 산책 알림 대상 조회
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void sendWalkAlarmNotification() {
        LocalTime targetTime = LocalTime.now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES);
        AlarmDay today = toAlarmDay(LocalDate.now().getDayOfWeek());

        List<PetWalkAlarm> alarms = petWalkAlarmRepository.findActiveAlarmsAtTimeAndDay(AlarmStatus.YES, targetTime, today);
        log.info("산책 알림 배치 실행 - 대상 시각: {}, 요일: {}, 알람 수: {}", targetTime, today, alarms.size());
        if (alarms.isEmpty()) return;

        List<FamilyMember> allMembers = fetchFamilyMembers(alarms);
        Map<Long, List<Push>> pushesByUserId = fetchPushesForWalkAlert(allMembers);
        Map<Long, List<FamilyMember>> membersByPetId = groupMembersByPetId(allMembers);

        List<SendPushServiceRequest> pushRequests = buildPushRequests(alarms, membersByPetId, pushesByUserId);
        publishIfNotEmpty(pushRequests);
    }

    // 알람 대상 펫들의 가족 구성원 일괄 조회
    private List<FamilyMember> fetchFamilyMembers(List<PetWalkAlarm> alarms) {
        List<Long> petIds = alarms.stream().map(a -> a.getPet().getId()).distinct().toList();
        return familyMemberRepository.findAllByPetIdsWithUser(petIds);
    }

    // 알림 설정(전체 OFF, 산책 OFF) 유저 제외 후 푸시 토큰 일괄 조회
    private Map<Long, List<Push>> fetchPushesForWalkAlert(List<FamilyMember> allMembers) {
        List<Long> userIds = allMembers.stream().map(fm -> fm.getUser().getId()).distinct().toList();
        Map<Long, AlertSetting> settingByUserId = alertSettingReadService.findAllByUserIds(userIds);

        Set<Long> targetUserIds = userIds.stream()
                .filter(uid -> {
                    AlertSetting s = settingByUserId.get(uid);
                    return s == null || (s.getAll() != AlertType.OFF && s.getWalk() == AlertType.ON);
                })
                .collect(Collectors.toSet());

        return pushRepository.findAllByUserIds(new ArrayList<>(targetUserIds)).stream()
                .collect(Collectors.groupingBy(p -> p.getUser().getId()));
    }

    // 가족 구성원 목록을 petId 기준으로 그룹화
    private Map<Long, List<FamilyMember>> groupMembersByPetId(List<FamilyMember> allMembers) {
        return allMembers.stream()
                .collect(Collectors.groupingBy(fm -> fm.getId().getPetId()));
    }

    // 알람별 가족 구성원 × 푸시 토큰 조합으로 전송 요청 목록 생성
    private List<SendPushServiceRequest> buildPushRequests(
            List<PetWalkAlarm> alarms,
            Map<Long, List<FamilyMember>> membersByPetId,
            Map<Long, List<Push>> pushesByUserId) {

        List<SendPushServiceRequest> pushRequests = new ArrayList<>();
        for (PetWalkAlarm alarm : alarms) {
            List<FamilyMember> members = membersByPetId.getOrDefault(alarm.getPet().getId(), List.of());
            for (FamilyMember fm : members) {
                List<Push> pushes = pushesByUserId.getOrDefault(fm.getUser().getId(), List.of());
                for (Push push : pushes) {
                    pushRequests.add(toWalkPushRequest(push, alarm));
                }
            }
        }
        return pushRequests;
    }

    // 산책 알림 단건 푸시 요청 생성
    private SendPushServiceRequest toWalkPushRequest(Push push, PetWalkAlarm alarm) {
        return SendPushServiceRequest.builder()
                .push(push)
                .sound("default")
                .body(alarm.getPet().getName() + " 산책 시간이 10분 후입니다!")
                .sendPushDataDto(SendPushDataDto.builder()
                        .alert_destination_type(AlertDestinationType.WALK)
                        .alert_destination_info(String.valueOf(alarm.getPet().getId()))
                        .build())
                .build();
    }

    // 푸시 요청이 있을 경우 비동기 이벤트 발행
    private void publishIfNotEmpty(List<SendPushServiceRequest> pushRequests) {
        if (!pushRequests.isEmpty()) {
            log.info("산책 알림 이벤트 발행 - 총 건수: {}", pushRequests.size());
            eventPublisher.publishEvent(new PushNotificationEvent(pushRequests));
        }
    }

    // DayOfWeek → AlarmDay 변환
    private AlarmDay toAlarmDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> AlarmDay.MON;
            case TUESDAY -> AlarmDay.TUE;
            case WEDNESDAY -> AlarmDay.WED;
            case THURSDAY -> AlarmDay.THU;
            case FRIDAY -> AlarmDay.FRI;
            case SATURDAY -> AlarmDay.SAT;
            case SUNDAY -> AlarmDay.SUN;
        };
    }
}
