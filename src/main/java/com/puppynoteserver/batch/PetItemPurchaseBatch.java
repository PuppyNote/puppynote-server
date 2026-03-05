package com.puppynoteserver.batch;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.firebase.FirebaseService;
import com.puppynoteserver.firebase.request.SendFirebaseDataDto;
import com.puppynoteserver.firebase.request.SendFirebaseServiceRequest;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import com.puppynoteserver.user.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetItemPurchaseBatch {

    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PushRepository pushRepository;
    private final FirebaseService firebaseService;

    // 매일 오전 8시 실행, 내일 구매 예정 용품 알림
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendPetItemPurchaseNotification() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<PetItemPurchase> latestPurchases = petItemPurchaseRepository.findAllLatestPurchases();
        log.info("구매용품 알림 배치 실행 - 내일 날짜: {}, 전체 아이템 수: {}", tomorrow, latestPurchases.size());

        List<PetItemPurchase> targets = latestPurchases.stream()
                .filter(p -> p.getPurchasedAt().plusDays(p.getPetItem().getPurchaseCycleDays()).equals(tomorrow))
                .toList();

        log.info("구매 예정 아이템 수: {}", targets.size());

        for (PetItemPurchase purchase : targets) {
            PetItem petItem = purchase.getPetItem();
            Long petId = petItem.getPet().getId();

            List<FamilyMember> familyMembers = familyMemberRepository.findByPetIdWithUser(petId);

            for (FamilyMember fm : familyMembers) {
                User user = fm.getUser();

                List<Push> pushes = pushRepository.findAllByUserId(user.getId());
                if (pushes.isEmpty()) {
                    log.warn("구매용품 알림 스킵 - userId: {}, 등록된 pushToken 없음", user.getId());
                    continue;
                }
                for (Push push : pushes) {
                    log.info("구매용품 알림 전송 시도 - userId: {}, deviceId: {}, pushToken: {}", user.getId(), push.getDeviceId(), push.getPushToken());
                    firebaseService.sendPushNotification(
                            SendFirebaseServiceRequest.builder()
                                    .push(push)
                                    .sound("default")
                                    .body(petItem.getName() + " 구매 예정일이 내일입니다!")
                                    .sendFirebaseDataDto(SendFirebaseDataDto.builder()
                                            .alert_destination_type(AlertDestinationType.PET_ITEM)
                                            .alert_destination_info(String.valueOf(petItem.getId()))
                                            .build())
                                    .build()
                    );
                }
            }
        }
    }
}
