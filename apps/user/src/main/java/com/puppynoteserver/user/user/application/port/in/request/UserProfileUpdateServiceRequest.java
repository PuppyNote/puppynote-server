package com.puppynoteserver.user.user.application.port.in.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfileUpdateServiceRequest {

    private final String nickName;
    private final String profileUrl;

    @Builder
    private UserProfileUpdateServiceRequest(String nickName, String profileUrl) {
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }
}
