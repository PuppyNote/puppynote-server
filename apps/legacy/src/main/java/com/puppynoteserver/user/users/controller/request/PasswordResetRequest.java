package com.puppynoteserver.user.users.controller.request;

import com.puppynoteserver.user.users.service.request.PasswordResetServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequest {

    @NotNull(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "변경할 비밀번호는 필수입니다.")
    private String newPassword;

    @Builder
    private PasswordResetRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public PasswordResetServiceRequest toServiceRequest() {
        return PasswordResetServiceRequest.builder()
                .email(email)
                .newPassword(newPassword)
                .build();
    }
}
