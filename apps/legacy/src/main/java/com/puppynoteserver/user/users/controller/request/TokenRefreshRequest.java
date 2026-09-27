package com.puppynoteserver.user.users.controller.request;

import com.puppynoteserver.user.users.service.request.TokenRefreshServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshRequest {

    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;

    public TokenRefreshServiceRequest toServiceRequest() {
        return TokenRefreshServiceRequest.of(refreshToken);
    }
}
