package com.puppynoteserver.user.user.adapter.out.client;

import org.springframework.stereotype.Component;

import com.puppynoteserver.user.user.application.port.out.OAuthApiClient;
import com.puppynoteserver.user.user.domain.enums.SnsType;

import lombok.RequiredArgsConstructor;

/**
 * [ADAPTER · OUT] Google OAuth2 - Bearer 토큰으로 이메일을 조회한다.
 */
@Component
@RequiredArgsConstructor
public class GoogleOAuthAdapter implements OAuthApiClient {

    private final GoogleApiFeignCall googleApiFeignCall;

    @Override
    public SnsType oAuthSnsType() {
        return SnsType.GOOGLE;
    }

    @Override
    public String getEmail(String accessToken) {
        return googleApiFeignCall.getUserInfo("Bearer " + accessToken).getEmail();
    }
}
