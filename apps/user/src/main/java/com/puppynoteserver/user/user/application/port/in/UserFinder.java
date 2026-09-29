package com.puppynoteserver.user.user.application.port.in;

import com.puppynoteserver.user.user.application.port.in.response.UserProfileResponse;
import com.puppynoteserver.user.user.domain.entity.User;

import java.util.List;

public interface UserFinder {
    User findByEmail(String email);
    User findById(Long userId);
    List<User> findAllByEmailLike(String email);
    UserProfileResponse getMyProfile();
}
