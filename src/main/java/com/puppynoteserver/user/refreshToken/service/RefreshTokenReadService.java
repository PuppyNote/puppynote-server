package com.puppynoteserver.user.refreshToken.service;

import com.puppynoteserver.user.refreshToken.entity.RefreshToken;

public interface RefreshTokenReadService {

    RefreshToken findByRefreshToken(String refreshToken);
}
