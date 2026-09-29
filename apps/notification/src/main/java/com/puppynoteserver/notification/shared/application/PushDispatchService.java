package com.puppynoteserver.notification.shared.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.notification.alertHistory.application.AlertHistoryService;
import com.puppynoteserver.notification.alertHistory.application.port.in.request.AlertHistoryServiceRequest;
import com.puppynoteserver.notification.shared.application.port.in.PushManager;
import com.puppynoteserver.notification.shared.application.port.out.PushSendCommand;
import com.puppynoteserver.notification.shared.application.port.out.PushSender;
import com.puppynoteserver.notification.shared.application.port.in.request.SendPushServiceRequest;
import com.puppynoteserver.notification.shared.domain.PushMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [APPLICATION] 푸시 발송 오케스트레이션 - push(토큰)·alertHistory(로그) 두 서브도메인을 가로지르므로
 * 어느 한쪽 소유가 아닌 shared에 둔다. chatplanet-server의 apps/push/shared에 있는
 * FcmSenderAdapter/PushHistoryWriter와 같은 위치·이유다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PushDispatchService implements PushManager {

    private final PushSender pushSender;
    private final AlertHistoryService alertHistoryService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendBatchPushNotification(List<SendPushServiceRequest> requests) {
        if (requests.isEmpty()) return;

        List<PushSendCommand> commands = buildCommands(requests);
        if (commands.isEmpty()) return;

        pushSender.sendBatch(commands);
        saveAlertHistories(requests);
    }

    private List<PushSendCommand> buildCommands(List<SendPushServiceRequest> requests) {
        return requests.stream()
                .filter(r -> r.getPush() != null && r.getPush().getPushToken() != null)
                .map(this::toCommand)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private PushSendCommand toCommand(SendPushServiceRequest request) {
        String title = PushMessage.from(request.getSendPushDataDto().getAlert_destination_type()).getText();
        Map<String, Object> data = objectMapper.convertValue(request.getSendPushDataDto(), Map.class);

        return new PushSendCommand(request.getPush().getPushToken(), title, request.getBody(), request.getSound(), data);
    }

    private void saveAlertHistories(List<SendPushServiceRequest> requests) {
        requests.stream()
                .filter(r -> r.getPush() != null)
                .collect(Collectors.toMap(
                        r -> r.getPush().getUserId(),
                        r -> r,
                        (existing, replacement) -> existing
                ))
                .values()
                .forEach(r -> alertHistoryService.createAlertHistory(
                        AlertHistoryServiceRequest.builder()
                                .userId(r.getPush().getUserId())
                                .alertDescription(r.getBody())
                                .alertDestinationType(r.getSendPushDataDto().getAlert_destination_type())
                                .alertDestinationInfo(r.getSendPushDataDto().getAlert_destination_info())
                                .build()
                ));
    }
}
