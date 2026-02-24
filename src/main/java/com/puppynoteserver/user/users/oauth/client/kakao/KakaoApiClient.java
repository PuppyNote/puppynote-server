package com.puppynoteserver.user.users.oauth.client.kakao;

import org.springframework.stereotype.Component;

import com.puppynoteserver.user.users.oauth.client.OAuthApiClient;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.oauth.feign.kakao.KakaoApiFeignCall;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient {

    private final KakaoApiFeignCall kakaoApiFeignCall;

    @Override
    public SnsType oAuthSnsType() {
        return SnsType.KAKAO;
    }

    @Override
    public String getEmail(String accessToken){
        return kakaoApiFeignCall.getUserInfo("Bearer " + accessToken).getEmail();
    }
}
