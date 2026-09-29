package com.puppynoteserver.pet.familyMembers.adapter.out.client;

import com.puppynoteserver.contracts.notification.api.NotificationApi;
import com.puppynoteserver.contracts.notification.api.NotificationType;
import com.puppynoteserver.contracts.notification.api.SendPushRequest;
import com.puppynoteserver.global.exception.InfrastructureException;
import com.puppynoteserver.pet.familyMembers.application.port.out.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * [ADAPTER · OUT] notification 서비스의 내부 푸시 발송 API를 호출한다.
 *
 * <p>
 * ⚠️ 실패해도 초대 자체를 막지 않는다 — 알림은 부가 기능이라 여기서 예외를 삼키고 로그만 남긴다.
 * FamilyMemberService.invite()가 이 호출 실패 때문에 트랜잭션을 롤백하면 안 된다.
 */
@Slf4j
@Component
public class NotificationClientAdapter implements NotificationSender {

    private final RestClient notificationRestClient;

    public NotificationClientAdapter(RestClient notificationRestClient) {
        this.notificationRestClient = notificationRestClient;
    }

    @Override
    public void sendFamilyInvite(Long inviteeUserId, String petName) {
        SendPushRequest request = new SendPushRequest(
                inviteeUserId,
                NotificationType.FAMILY_INVITE,
                petName + " 가족으로 초대되었습니다.",
                petName
        );

        try {
            notificationRestClient.post().uri(NotificationApi.PUSH).body(request).retrieve().toBodilessEntity();
        } catch (RestClientException exception) {
            // 알림 발송 실패는 초대 자체를 실패시킬 이유가 아니다.
            log.warn("가족 초대 푸시 발송 실패 - userId: {}, message: {}", inviteeUserId, exception.getMessage());
        }
    }
}
