package com.puppynoteserver.user.refreshToken.repository;

import com.puppynoteserver.user.refreshToken.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllInBatch();
}
