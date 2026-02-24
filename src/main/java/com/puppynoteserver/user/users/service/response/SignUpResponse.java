package com.puppynoteserver.user.users.service.response;

import com.puppynoteserver.user.users.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponse {
    private final String email;
    private final String nickName;

    @Builder
    private SignUpResponse(String email, String nickName) {
        this.email = email;
        this.nickName = nickName;
    }

    public static SignUpResponse of(User user) {
        return SignUpResponse.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .build();
    }
}
