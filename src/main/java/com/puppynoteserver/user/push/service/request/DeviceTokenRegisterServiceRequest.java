package com.puppynoteserver.user.push.service.request;

import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.entity.enums.DevicePlatform;
import com.puppynoteserver.user.users.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeviceTokenRegisterServiceRequest {

	private final String deviceId;
	private final String pushToken;
	private final DevicePlatform platform;

	@Builder
	private DeviceTokenRegisterServiceRequest(String deviceId, String pushToken, DevicePlatform platform) {
		this.deviceId = deviceId;
		this.pushToken = pushToken;
		this.platform = platform;
	}

	public Push toEntity(User user) {
		return Push.of(deviceId, user, pushToken, platform);
	}
}
