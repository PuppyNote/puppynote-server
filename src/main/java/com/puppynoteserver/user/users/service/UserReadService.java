package com.puppynoteserver.user.users.service;

import com.puppynoteserver.user.users.entity.User;

public interface UserReadService {
    User findByEmail(String email);
    User findById(Long userId);
}
