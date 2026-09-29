package com.puppynoteserver.user.user.adapter.in.web.request;

import com.puppynoteserver.user.user.application.port.in.request.EmailSendServiceRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailSendRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Builder
    private EmailSendRequest(String email) {
        this.email = email;
    }

    public EmailSendServiceRequest toServiceRequest() {
        return EmailSendServiceRequest.builder()
                .email(email)
                .build();
    }
}
