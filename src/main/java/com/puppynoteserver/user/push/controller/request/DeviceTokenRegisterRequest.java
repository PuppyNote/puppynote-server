package com.puppynoteserver.user.push.controller.request;

import com.puppynoteserver.user.push.entity.enums.DevicePlatform;
import com.puppynoteserver.user.push.service.request.DeviceTokenRegisterServiceRequest;
import jakarta.validation.constraints.NotBlank;
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

	/**
	 * 역직렬화 시점에 enum 변환이 일어나면 잘못된 값이 500으로 응답되므로,
	 * 문자열로 받아 {@link #toServiceRequest()}에서 변환한다.
	 */
	@NotBlank(message = "플랫폼은 필수입니다.")
	private String platform;

	@Builder
	private DeviceTokenRegisterRequest(String deviceId, String pushToken, String platform) {
		this.deviceId = deviceId;
		this.pushToken = pushToken;
		this.platform = platform;
	}

	public DeviceTokenRegisterServiceRequest toServiceRequest() {
		return DeviceTokenRegisterServiceRequest.builder()
			.deviceId(deviceId)
			.pushToken(pushToken)
			.platform(DevicePlatform.from(platform))
			.build();
	}
}
