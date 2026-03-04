package com.puppynoteserver.user.users.controller.request;

import com.puppynoteserver.user.users.service.request.UserProfileUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickName;

    private String profileUrl;

    @Builder
    private UserProfileUpdateRequest(String nickName, String profileUrl) {
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }

    public UserProfileUpdateServiceRequest toServiceRequest() {
        return UserProfileUpdateServiceRequest.builder()
                .nickName(nickName)
                .profileUrl(profileUrl)
                .build();
    }
}
