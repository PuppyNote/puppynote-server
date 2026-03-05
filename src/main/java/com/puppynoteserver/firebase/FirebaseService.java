package com.puppynoteserver.firebase;

import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertHistory.service.alertHistory.request.AlertHistoryServiceRequest;
import com.puppynoteserver.firebase.enums.PushMessage;
import com.puppynoteserver.firebase.request.SendFirebaseDataDto;
import com.puppynoteserver.firebase.request.SendFirebaseServiceRequest;
import com.puppynoteserver.user.push.entity.Push;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FirebaseService {

    private final FirebaseMessaging firebaseMessaging;
    private final AlertHistoryService alertHistoryService;
    private final ObjectMapper objectMapper;

    public void sendPushNotification(SendFirebaseServiceRequest sendPushServiceRequest) {
        sendFcm(sendPushServiceRequest.getPush(), sendPushServiceRequest);
        // AlertHistory 저장
        alertHistoryService.createAlertHistory(AlertHistoryServiceRequest.of(sendPushServiceRequest));
    }

    // 여러 디바이스에 발송하되 AlertHistory는 사용자당 한 번만 저장
    public void sendPushNotificationToAll(List<Push> pushes, SendFirebaseServiceRequest baseRequest) {
        for (Push push : pushes) {
            sendFcm(push, baseRequest);
        }
        // AlertHistory는 첫 번째 push의 User 기준으로 한 번만 저장
        if (!pushes.isEmpty()) {
            SendFirebaseServiceRequest requestForHistory = SendFirebaseServiceRequest.builder()
                    .push(pushes.get(0))
                    .sound(baseRequest.getSound())
                    .body(baseRequest.getBody())
                    .sendFirebaseDataDto(baseRequest.getSendFirebaseDataDto())
                    .build();
            alertHistoryService.createAlertHistory(AlertHistoryServiceRequest.of(requestForHistory));
        }
    }

    private void sendFcm(Push push, SendFirebaseServiceRequest sendPushServiceRequest) {
        try {
            if (push == null) return;

            Map<String, Object> customData = ObjectToMap(sendPushServiceRequest.getSendFirebaseDataDto());
            Map<String, String> stringData = customData.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue() == null ? "" : e.getValue().toString()
                    ));

            String title = PushMessage.from(sendPushServiceRequest.getSendFirebaseDataDto().getAlert_destination_type()).getText();

            Message message = Message.builder()
                    // iOS/Android 공통 알림
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(sendPushServiceRequest.getBody())
                            .build())
                    // iOS 전용 설정
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .putAllCustomData(customData)
                                    .setSound(sendPushServiceRequest.getSound())
                                    .build())
                            .build())
                    // Android 전용 설정
                    .setAndroidConfig(AndroidConfig.builder()
                            .putAllData(stringData)
                            .setNotification(AndroidNotification.builder()
                                    .setSound(sendPushServiceRequest.getSound())
                                    .build())
                            .build())
                    .setToken(push.getPushToken())
                    .build();

            String messageId = firebaseMessaging.send(message);
            log.info("푸시 전송 성공 - token: {}, messageId: {}", push.getPushToken(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("푸시 전송 실패 (FCM) - token: {}, errorCode: {}, message: {}",
                    push.getPushToken(), e.getMessagingErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("푸시 전송 실패 (기타 예외) - token: {}, exceptionType: {}, message: {}",
                    push.getPushToken(), e.getClass().getName(), e.getMessage());
        }
    }

    private Map<String, Object> ObjectToMap(SendFirebaseDataDto sendFirebaseDataDto) {
        return objectMapper.convertValue(sendFirebaseDataDto, Map.class);
    }
}
