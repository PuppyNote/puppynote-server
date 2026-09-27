package com.puppynoteserver.user.users.service.response;

import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.user.users.entity.enums.SettingStatus;
import com.puppynoteserver.user.users.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class OAuthLoginResponse {

	private final String email;
	private final String accessToken;
	private final String refreshToken;
	private final SettingStatus settingStatus;

	@Builder
	private OAuthLoginResponse(String email, String accessToken, String refreshToken, SettingStatus settingStatus) {
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.settingStatus = settingStatus;
	}

	public static OAuthLoginResponse of(User user, JwtToken jwtToken) {
		return OAuthLoginResponse.builder()
			.email(user.getEmail())
			.accessToken(jwtToken.getAccessToken())
			.refreshToken(jwtToken.getRefreshToken())
			.build();
	}
}
