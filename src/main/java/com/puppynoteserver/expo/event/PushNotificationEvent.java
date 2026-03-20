package com.puppynoteserver.expo.event;

import com.puppynoteserver.expo.request.SendPushServiceRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class PushNotificationEvent {

    private final List<SendPushServiceRequest> requests;

    public PushNotificationEvent(List<SendPushServiceRequest> requests) {
        this.requests = requests;
    }
}
