package com.puppynoteserver.user.push.controller.request;

import com.puppynoteserver.user.push.entity.enums.DevicePlatform;
import com.puppynoteserver.user.push.service.request.DeviceTokenRegisterServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeviceTokenRegisterRequest {

	@NotBlank(message = "디바이스ID는 필수입니다.")
	private String deviceId;

	@NotBlank(message = "디바이스 토큰은 필수입니다.")
	private String pushToken;

	@NotNull(message = "플랫폼은 필수입니다.")
	private DevicePlatform platform;

	@Builder
	private DeviceTokenRegisterRequest(String deviceId, String pushToken, DevicePlatform platform) {
		this.deviceId = deviceId;
		this.pushToken = pushToken;
		this.platform = platform;
	}

	public DeviceTokenRegisterServiceRequest toServiceRequest() {
		return DeviceTokenRegisterServiceRequest.builder()
			.deviceId(deviceId)
			.pushToken(pushToken)
			.platform(platform)
			.build();
	}
}
