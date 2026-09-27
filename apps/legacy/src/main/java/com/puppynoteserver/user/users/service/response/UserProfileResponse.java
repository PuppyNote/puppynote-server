package com.puppynoteserver.user.users.service.response;

import com.puppynoteserver.user.users.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private final Long userId;
    private final String email;
    private final String nickName;
    private final String profileUrl;

    private UserProfileResponse(Long userId, String email, String nickName, String profileUrl) {
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }

    public static UserProfileResponse of(User user, String profileUrl) {
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getNickName(), profileUrl);
    }
}
