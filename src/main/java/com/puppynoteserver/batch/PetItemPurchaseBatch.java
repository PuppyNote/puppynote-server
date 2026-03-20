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
import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetItemPurchaseBatch {

    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PushRepository pushRepository;
    private final AlertSettingReadService alertSettingReadService;
    private final ApplicationEventPublisher eventPublisher;

    // 매일 오전 8시 실행, 내일 구매 예정 용품 알림
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendPetItemPurchaseNotification() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<PetItemPurchase> targets = fetchTargets(tomorrow);
        log.info("구매용품 알림 배치 실행 - 내일 날짜: {}, 구매 예정 아이템 수: {}", tomorrow, targets.size());
        if (targets.isEmpty()) return;

        List<FamilyMember> allMembers = fetchFamilyMembers(targets);
        Map<Long, List<Push>> pushesByUserId = fetchPushesForAlertOnUsers(allMembers);
        Map<Long, List<FamilyMember>> membersByPetId = groupMembersByPetId(allMembers);

        List<SendPushServiceRequest> pushRequests = buildPushRequests(targets, membersByPetId, pushesByUserId);
        publishIfNotEmpty(pushRequests);
    }

    // 전체 최신 구매 이력 중 내일 구매 예정인 아이템 필터링
    private List<PetItemPurchase> fetchTargets(LocalDate tomorrow) {
        return petItemPurchaseRepository.findAllLatestPurchases().stream()
                .filter(p -> p.getPurchasedAt().plusDays(p.getPetItem().getPurchaseCycleDays()).equals(tomorrow))
                .toList();
    }

    // 대상 펫들의 가족 구성원 일괄 조회
    private List<FamilyMember> fetchFamilyMembers(List<PetItemPurchase> targets) {
        List<Long> petIds = targets.stream().map(p -> p.getPetItem().getPet().getId()).distinct().toList();
        return familyMemberRepository.findAllByPetIdsWithUser(petIds);
    }

    // 알림 설정 전체 OFF 유저 제외 후 푸시 토큰 일괄 조회
    private Map<Long, List<Push>> fetchPushesForAlertOnUsers(List<FamilyMember> allMembers) {
        List<Long> userIds = allMembers.stream().map(fm -> fm.getUser().getId()).distinct().toList();
        Map<Long, AlertSetting> settingByUserId = alertSettingReadService.findAllByUserIds(userIds);

        Set<Long> alertOnUserIds = userIds.stream()
                .filter(uid -> {
                    AlertSetting s = settingByUserId.get(uid);
                    return s == null || s.getAll() != AlertType.OFF;
                })
                .collect(Collectors.toSet());

        return pushRepository.findAllByUserIds(new ArrayList<>(alertOnUserIds)).stream()
                .collect(Collectors.groupingBy(p -> p.getUser().getId()));
    }

    // 가족 구성원 목록을 petId 기준으로 그룹화
    private Map<Long, List<FamilyMember>> groupMembersByPetId(List<FamilyMember> allMembers) {
        return allMembers.stream()
                .collect(Collectors.groupingBy(fm -> fm.getId().getPetId()));
    }

    // 구매 예정 아이템별 가족 구성원 × 푸시 토큰 조합으로 전송 요청 목록 생성
    private List<SendPushServiceRequest> buildPushRequests(
            List<PetItemPurchase> targets,
            Map<Long, List<FamilyMember>> membersByPetId,
            Map<Long, List<Push>> pushesByUserId) {

        List<SendPushServiceRequest> pushRequests = new ArrayList<>();
        for (PetItemPurchase purchase : targets) {
            PetItem petItem = purchase.getPetItem();
            List<FamilyMember> members = membersByPetId.getOrDefault(petItem.getPet().getId(), List.of());
            for (FamilyMember fm : members) {
                List<Push> pushes = pushesByUserId.getOrDefault(fm.getUser().getId(), List.of());
                for (Push push : pushes) {
                    pushRequests.add(toPetItemPushRequest(push, petItem));
                }
            }
        }
        return pushRequests;
    }

    // 용품 구매 알림 단건 푸시 요청 생성
    private SendPushServiceRequest toPetItemPushRequest(Push push, PetItem petItem) {
        return SendPushServiceRequest.builder()
                .push(push)
                .sound("default")
                .body(petItem.getName() + " 구매 예정일이 내일입니다!")
                .sendPushDataDto(SendPushDataDto.builder()
                        .alert_destination_type(AlertDestinationType.PET_ITEM)
                        .alert_destination_info(String.valueOf(petItem.getId()))
                        .build())
                .build();
    }

    // 푸시 요청이 있을 경우 비동기 이벤트 발행
    private void publishIfNotEmpty(List<SendPushServiceRequest> pushRequests) {
        if (!pushRequests.isEmpty()) {
            log.info("구매용품 알림 이벤트 발행 - 총 건수: {}", pushRequests.size());
            eventPublisher.publishEvent(new PushNotificationEvent(pushRequests));
        }
    }
}
