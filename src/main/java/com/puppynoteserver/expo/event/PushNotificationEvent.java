package com.puppynoteserver.expo.event;

import com.puppynoteserver.expo.request.SendPushServiceRequest;
import com.puppynoteserver.user.push.entity.Push;
import lombok.Getter;

import java.util.List;

@Getter
public class PushNotificationEvent {

    private final List<Push> pushes;
    private final SendPushServiceRequest request;

    public PushNotificationEvent(List<Push> pushes, SendPushServiceRequest request) {
        this.pushes = pushes;
        this.request = request;
    }
}
