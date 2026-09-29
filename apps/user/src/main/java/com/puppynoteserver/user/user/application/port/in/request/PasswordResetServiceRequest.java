package com.puppynoteserver.user.user.application.port.in.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordResetServiceRequest {

    private final String email;
    private final String newPassword;

    @Builder
    private PasswordResetServiceRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
}
