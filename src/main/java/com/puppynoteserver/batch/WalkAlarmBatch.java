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
import com.puppynoteserver.user.users.entity.User;
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

        List<SendPushServiceRequest> pushRequests = new ArrayList<>();

        for (PetWalkAlarm alarm : alarms) {
            List<FamilyMember> familyMembers = familyMemberRepository.findByPetIdWithUser(alarm.getPet().getId());

            for (FamilyMember fm : familyMembers) {
                User user = fm.getUser();
                AlertSetting alertSetting = alertSettingReadService.findByUserOrCreateDefault(user);

                log.info("산책 알림 대상 - userId: {}, walkAlertSetting: {}", user.getId(), alertSetting.getWalk());

                if (alertSetting.getWalk() != AlertType.ON) {
                    log.info("산책 알림 스킵 - userId: {}, walk 설정이 OFF", user.getId());
                    continue;
                }

                List<Push> pushes = pushRepository.findAllByUserId(user.getId());
                if (pushes.isEmpty()) {
                    log.warn("산책 알림 스킵 - userId: {}, 등록된 pushToken 없음", user.getId());
                    continue;
                }

                for (Push push : pushes) {
                    pushRequests.add(SendPushServiceRequest.builder()
                            .push(push)
                            .sound("default")
                            .body(alarm.getPet().getName() + " 산책 시간이 10분 후입니다!")
                            .sendPushDataDto(SendPushDataDto.builder()
                                    .alert_destination_type(AlertDestinationType.WALK)
                                    .alert_destination_info(String.valueOf(alarm.getPet().getId()))
                                    .build())
                            .build());
                }
            }
        }

        if (!pushRequests.isEmpty()) {
            log.info("산책 알림 이벤트 발행 - 총 건수: {}", pushRequests.size());
            eventPublisher.publishEvent(new PushNotificationEvent(pushRequests));
        }
    }

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
