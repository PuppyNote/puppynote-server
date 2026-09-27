package com.puppynoteserver.user.users.oauth.feign.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.puppynoteserver.user.users.oauth.feign.kakao.response.KakaoUserInfoResponse;

@Component
@FeignClient(name="${oauth.kakao.api.name}", url="${oauth.kakao.api.url}", configuration = FeignClientsConfiguration.class)
public interface KakaoApiFeignCall {

    @PostMapping(value = "/v2/user/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String auth);

}
