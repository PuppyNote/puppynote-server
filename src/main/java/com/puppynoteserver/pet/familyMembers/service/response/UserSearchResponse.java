package com.puppynoteserver.pet.familyMembers.service.response;

import com.puppynoteserver.user.users.entity.User;
import lombok.Getter;

@Getter
public class UserSearchResponse {

    private final Long userId;
    private final String email;
    private final String nickName;
    private final String profileUrl;

    private UserSearchResponse(Long userId, String email, String nickName, String profileUrl) {
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
    }

    public static UserSearchResponse of(User user) {
        return new UserSearchResponse(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                user.getProfileUrl()
        );
    }
}
