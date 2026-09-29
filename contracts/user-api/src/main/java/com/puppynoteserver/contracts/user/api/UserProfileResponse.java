package com.puppynoteserver.contracts.user.api;

public record UserProfileResponse(
        Long userId,
        String email,
        String nickName,
        String profileUrl
) {
}
