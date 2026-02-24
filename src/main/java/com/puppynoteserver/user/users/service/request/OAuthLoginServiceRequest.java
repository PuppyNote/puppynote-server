package com.puppynoteserver.user.users.service.request;

import com.puppynoteserver.user.users.entity.enums.SnsType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
