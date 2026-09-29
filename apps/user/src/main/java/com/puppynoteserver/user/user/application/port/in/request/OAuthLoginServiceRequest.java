package com.puppynoteserver.user.user.application.port.in.request;


import com.puppynoteserver.user.user.domain.enums.SnsType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginServiceRequest {
	private final String token;
	private final SnsType snsType;
	private final String deviceId;
	private final String pushKey;

	@Builder
	private OAuthLoginServiceRequest(String token, SnsType snsType, String deviceId, String pushKey) {
		this.token = token;
		this.snsType = snsType;
		this.deviceId = deviceId;
		this.pushKey = pushKey;
	}
}
