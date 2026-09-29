package com.puppynoteserver.user.user.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.puppynoteserver.user.user.adapter.out.client.dto.KakaoUserInfoResponse;

/**
 * [ADAPTER · OUT] Kakao 로그인 사용자 정보 조회 호출 정보.
 */
@Component
@FeignClient(name = "${oauth.kakao.api.name}", url = "${oauth.kakao.api.url}", configuration = FeignClientsConfiguration.class)
public interface KakaoApiFeignCall {

    @PostMapping(value = "/v2/user/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String auth);

}
