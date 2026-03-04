package com.puppynoteserver.user.users.service.response;

import com.puppynoteserver.user.users.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private final String email;
    private final String nickName;
    private final String profileUrl;

    private UserProfileResponse(String email, String nickName, String profileUrl) {
        this.email = email;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }

    public static UserProfileResponse of(User user) {
        return new UserProfileResponse(user.getEmail(), user.getNickName(), user.getProfileUrl());
    }
}
