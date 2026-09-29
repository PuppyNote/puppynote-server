package com.puppynoteserver.user.user.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.user.adapter.in.web.request.*;
import com.puppynoteserver.user.user.application.port.in.LoginManager;
import com.puppynoteserver.user.user.application.port.in.response.LoginResponse;
import com.puppynoteserver.user.user.application.port.in.response.OAuthLoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginManager loginService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        return ApiResponse.ok(loginService.normalLogin(loginRequest.toServiceRequest()));
    }

    @PostMapping("/oauth/login")
    public ApiResponse<OAuthLoginResponse> oauthLogin(@Valid @RequestBody LoginOauthRequest loginOauthRequest) throws JsonProcessingException {
        return ApiResponse.ok(loginService.oauthLogin(loginOauthRequest.toServiceRequest()));
    }

    @PostMapping("/password/email/send")
    public ApiResponse<String> sendPasswordResetEmail(@Valid @RequestBody EmailSendRequest request) {
        return ApiResponse.ok(loginService.sendPasswordResetEmail(request.toServiceRequest()));
    }

    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        loginService.resetPassword(request.toServiceRequest());
        return ApiResponse.ok(null);
    }

}
