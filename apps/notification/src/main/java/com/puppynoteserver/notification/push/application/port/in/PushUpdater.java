package com.puppynoteserver.notification.push.application.port.in;

public interface PushUpdater {

    void upsertByDeviceId(String deviceId, Long userId, String pushToken);
}
