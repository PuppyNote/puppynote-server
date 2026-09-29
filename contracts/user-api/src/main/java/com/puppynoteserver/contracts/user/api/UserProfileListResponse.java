package com.puppynoteserver.contracts.user.api;

import java.util.List;

public record UserProfileListResponse(
        List<UserProfileResponse> users
) {
}
