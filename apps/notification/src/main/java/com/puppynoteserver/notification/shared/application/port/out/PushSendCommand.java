package com.puppynoteserver.notification.shared.application.port.out;

import java.util.Map;

/**
 * [APPLICATION · OUT PORT] 외부 푸시 제공자에게 보낼 발송 1건.
 */
public record PushSendCommand(
        String pushToken,
        String title,
        String body,
        String sound,
        Map<String, Object> data
) {
}
