package com.puppynoteserver.pet.familyMembers.application.port.in.response;

import com.puppynoteserver.pet.familyMembers.application.port.out.UserProfileReader.UserProfile;
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

    public static UserSearchResponse of(UserProfile profile, String presignedProfileUrl) {
        return new UserSearchResponse(
                profile.userId(),
                profile.email(),
                profile.nickName(),
                presignedProfileUrl
        );
    }
}
