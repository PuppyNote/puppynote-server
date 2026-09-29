package com.puppynoteserver.user.session.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.session.adapter.in.web.request.TokenRefreshRequest;
import com.puppynoteserver.user.session.application.port.in.SessionManager;
import com.puppynoteserver.user.session.application.port.in.response.TokenRefreshResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class SessionController {

    private final SessionManager sessionManager;

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.ok(sessionManager.refresh(request.toServiceRequest()));
    }
}
