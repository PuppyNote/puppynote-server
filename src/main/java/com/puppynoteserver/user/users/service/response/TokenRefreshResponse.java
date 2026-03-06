package com.puppynoteserver.user.users.service.response;

import com.puppynoteserver.jwt.dto.JwtToken;
import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    private final String accessToken;
    private final String refreshToken;

    private TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenRefreshResponse from(JwtToken jwtToken) {
        return new TokenRefreshResponse(jwtToken.getAccessToken(), jwtToken.getRefreshToken());
    }
}
