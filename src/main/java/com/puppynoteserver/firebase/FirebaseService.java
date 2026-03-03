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
        try {
            // PushToken이 있으면 푸시전송
            Push push = sendPushServiceRequest.getPush();
            if (push != null) {
                Map<String, Object> customData = ObjectToMap(sendPushServiceRequest.getSendFirebaseDataDto());
                // String으로 변환하여 Android data payload에도 사용
                Map<String, String> stringData = customData.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue() == null ? "" : e.getValue().toString()
                        ));

                Message message = Message.builder()
                        // iOS/Android 공통 알림
                        .setNotification(Notification.builder()
                                .setTitle(PushMessage.TITLE.getText())
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
            }
        } catch (FirebaseMessagingException e) {
            log.error("푸시 전송 실패 (FCM) - token: {}, errorCode: {}, message: {}",
                    sendPushServiceRequest.getPush().getPushToken(), e.getMessagingErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("푸시 전송 실패 (기타 예외) - token: {}, exceptionType: {}, message: {}",
                    sendPushServiceRequest.getPush().getPushToken(), e.getClass().getName(), e.getMessage());
        } finally {
            //히스토리는 무조건 쌓는걸로
            alertHistoryService.createAlertHistory(AlertHistoryServiceRequest.of(sendPushServiceRequest));
        }

    }

    private Map<String, Object> ObjectToMap(SendFirebaseDataDto sendFirebaseDataDto) {
        return objectMapper.convertValue(sendFirebaseDataDto, Map.class);
    }
}
