package com.puppynoteserver.notification.shared.event;

import com.puppynoteserver.notification.shared.application.port.in.PushManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationEventListener {

    private final PushManager pushManager;

    @Async
    @EventListener
    public void handlePushNotification(PushNotificationEvent event) {
        log.info("푸시 알림 이벤트 수신 - 건수: {}", event.getRequests().size());
        pushManager.sendBatchPushNotification(event.getRequests());
    }
}
