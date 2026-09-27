package com.puppynoteserver.user.users.oauth.feign.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.puppynoteserver.global.config.FeignConfig;
import com.puppynoteserver.user.users.oauth.feign.google.response.GoogleUserInfoResponse;

@Component
@FeignClient(name = "${oauth.google.api.name}", url = "${oauth.google.api.url}", configuration = FeignConfig.class)
public interface GoogleApiFeignCall {

	@GetMapping(value = "/oauth2/v1/userinfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	GoogleUserInfoResponse getUserInfo(@RequestHeader("Authorization") String auth);
}
