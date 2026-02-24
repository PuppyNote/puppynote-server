package com.puppynoteserver.user.users.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpServiceRequest {
    private final String email;
    private final String password;
    private final String nickName;

    @Builder
    private SignUpServiceRequest(String email, String password, String nickName) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
    }
}
