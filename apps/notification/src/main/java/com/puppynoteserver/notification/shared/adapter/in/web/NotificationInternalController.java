package com.puppynoteserver.notification.shared.adapter.in.web;

import com.puppynoteserver.contracts.notification.api.NotificationApi;
import com.puppynoteserver.contracts.notification.api.NotificationType;
import com.puppynoteserver.contracts.notification.api.SendPushRequest;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertDestinationType;
import com.puppynoteserver.notification.push.application.port.in.PushFinder;
import com.puppynoteserver.notification.push.domain.entity.Push;
import com.puppynoteserver.notification.shared.application.port.in.PushManager;
import com.puppynoteserver.notification.shared.application.port.in.request.SendPushDataDto;
import com.puppynoteserver.notification.shared.application.port.in.request.SendPushServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * [ADAPTER · IN] 다른 서비스가 유저 한 명에게 푸시 발송을 요청할 때 쓰는 내부 전용 API.
 *
 * <p>
 * {@code /internal/**} 경로라 인증 없이 호출 가능하다 (SecurityConfig 참고). contracts:notification-api가
 * 이 요청 계약({@link NotificationType} 포함)을 소유한다 — notification 내부의
 * {@link AlertDestinationType}과는 별개 타입이라 여기서 직접 매핑한다.
 */
@RestController
@RequiredArgsConstructor
public class NotificationInternalController {

    private static final String DEFAULT_SOUND = "default";

    private final PushFinder pushFinder;
    private final PushManager pushManager;

    @PostMapping(NotificationApi.PUSH)
    public ResponseEntity<Void> sendPush(@RequestBody SendPushRequest request) {
        Optional<Push> push = pushFinder.findByUserId(request.userId());
        if (push.isEmpty()) {
            // 발송할 디바이스가 없을 뿐 에러는 아니다.
            return ResponseEntity.noContent().build();
        }

        SendPushServiceRequest serviceRequest = SendPushServiceRequest.builder()
                .push(push.get())
                .sound(DEFAULT_SOUND)
                .body(request.description())
                .sendPushDataDto(SendPushDataDto.builder()
                        .alert_destination_type(toAlertDestinationType(request.type()))
                        .alert_destination_info(request.destinationInfo())
                        .build())
                .build();

        pushManager.sendBatchPushNotification(List.of(serviceRequest));
        return ResponseEntity.noContent().build();
    }

    private AlertDestinationType toAlertDestinationType(NotificationType type) {
        return switch (type) {
            case DAILY_REPORT -> AlertDestinationType.DAILY_REPORT;
            case FRIEND -> AlertDestinationType.FRIEND;
            case FRIEND_CODE -> AlertDestinationType.FRIEND_CODE;
            case WALK -> AlertDestinationType.WALK;
            case PET_ITEM -> AlertDestinationType.PET_ITEM;
            case FAMILY_INVITE -> AlertDestinationType.FAMILY_INVITE;
        };
    }
}
