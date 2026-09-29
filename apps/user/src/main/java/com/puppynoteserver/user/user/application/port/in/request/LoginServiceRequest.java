package com.puppynoteserver.user.user.application.port.in.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginServiceRequest {
    private final String email;
    private final String password;
    private final String deviceId;

    @Builder
    private LoginServiceRequest(String email, String password, String deviceId) {
        this.email = email;
        this.password = password;
        this.deviceId = deviceId;
    }
}
