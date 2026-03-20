package com.puppynoteserver.expo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertHistory.service.alertHistory.request.AlertHistoryServiceRequest;
import com.puppynoteserver.expo.enums.PushMessage;
import com.puppynoteserver.expo.request.SendPushDataDto;
import com.puppynoteserver.expo.request.SendPushServiceRequest;
import com.puppynoteserver.user.push.entity.Push;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExpoNotiService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;
    private final AlertHistoryService alertHistoryService;
    private final ObjectMapper objectMapper;

    public void sendPushNotification(SendPushServiceRequest request) {
        sendExpo(request.getPush(), request);
        alertHistoryService.createAlertHistory(AlertHistoryServiceRequest.of(request));
    }

    public void sendPushNotificationToAll(List<Push> pushes, SendPushServiceRequest baseRequest) {
        for (Push push : pushes) {
            sendExpo(push, baseRequest);
        }
        if (!pushes.isEmpty()) {
            SendPushServiceRequest requestForHistory = SendPushServiceRequest.builder()
                    .push(pushes.get(0))
                    .sound(baseRequest.getSound())
                    .body(baseRequest.getBody())
                    .sendPushDataDto(baseRequest.getSendPushDataDto())
                    .build();
            alertHistoryService.createAlertHistory(AlertHistoryServiceRequest.of(requestForHistory));
        }
    }

    private void sendExpo(Push push, SendPushServiceRequest request) {
        if (push == null || push.getPushToken() == null) return;

        try {
            String title = PushMessage.from(request.getSendPushDataDto().getAlert_destination_type()).getText();
            Map<String, Object> data = objectMapper.convertValue(request.getSendPushDataDto(), Map.class);

            Map<String, Object> body = Map.of(
                    "to", push.getPushToken(),
                    "title", title,
                    "body", request.getBody(),
                    "sound", request.getSound(),
                    "data", data
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, entity, String.class);

            log.info("Expo 푸시 전송 성공 - token: {}, status: {}", push.getPushToken(), response.getStatusCode());
        } catch (Exception e) {
            log.error("Expo 푸시 전송 실패 - token: {}, message: {}", push.getPushToken(), e.getMessage());
        }
    }
}
