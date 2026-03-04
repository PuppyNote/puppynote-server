package com.puppynoteserver.user.users.service;

import com.puppynoteserver.user.users.entity.User;

import java.util.List;

public interface UserReadService {
    User findByEmail(String email);
    User findById(Long userId);
    List<User> findAllByEmailLike(String email);
}
