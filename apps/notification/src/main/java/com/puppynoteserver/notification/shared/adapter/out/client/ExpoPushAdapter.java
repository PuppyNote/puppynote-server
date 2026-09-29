package com.puppynoteserver.notification.shared.adapter.out.client;

import com.puppynoteserver.notification.shared.application.port.out.PushSendCommand;
import com.puppynoteserver.notification.shared.application.port.out.PushSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * [ADAPTER · OUT] Expo Push API 호출.
 */
@Slf4j
@Component
public class ExpoPushAdapter implements PushSender {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;

    public ExpoPushAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendBatch(List<PushSendCommand> commands) {
        if (commands.isEmpty()) return;

        List<Map<String, Object>> bodies = commands.stream().map(this::toPushBody).toList();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(bodies, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, entity, String.class);
            log.info("Expo 배치 푸시 전송 성공 - 건수: {}, status: {}", bodies.size(), response.getStatusCode());
        } catch (Exception e) {
            log.error("Expo 배치 푸시 전송 실패 - message: {}", e.getMessage());
        }
    }

    private Map<String, Object> toPushBody(PushSendCommand command) {
        return Map.of(
                "to", command.pushToken(),
                "title", command.title(),
                "body", command.body(),
                "sound", command.sound(),
                "data", command.data()
        );
    }
}
