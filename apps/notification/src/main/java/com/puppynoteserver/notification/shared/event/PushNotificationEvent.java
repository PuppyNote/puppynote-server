package com.puppynoteserver.notification.shared.event;

import com.puppynoteserver.notification.shared.application.port.in.request.SendPushServiceRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class PushNotificationEvent {

    private final List<SendPushServiceRequest> requests;

    public PushNotificationEvent(List<SendPushServiceRequest> requests) {
        this.requests = requests;
    }
}
