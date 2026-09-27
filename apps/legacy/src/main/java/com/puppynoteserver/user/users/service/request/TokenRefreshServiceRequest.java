package com.puppynoteserver.user.users.service.request;

import lombok.Getter;

@Getter
public class TokenRefreshServiceRequest {

    private final String refreshToken;

    private TokenRefreshServiceRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static TokenRefreshServiceRequest of(String refreshToken) {
        return new TokenRefreshServiceRequest(refreshToken);
    }
}
