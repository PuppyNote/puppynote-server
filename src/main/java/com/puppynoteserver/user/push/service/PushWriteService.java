package com.puppynoteserver.user.push.service;

import com.puppynoteserver.user.push.service.request.DeviceTokenRegisterServiceRequest;
import com.puppynoteserver.user.users.entity.User;

public interface PushWriteService {

    void upsertByDeviceId(String deviceId, User user, String pushToken);

    void registerDeviceToken(DeviceTokenRegisterServiceRequest request);
}
