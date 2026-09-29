package com.puppynoteserver.notification.shared.application.port.in;

import com.puppynoteserver.notification.shared.application.port.in.request.SendPushServiceRequest;

import java.util.List;

public interface PushManager {
    void sendBatchPushNotification(List<SendPushServiceRequest> requests);
}
