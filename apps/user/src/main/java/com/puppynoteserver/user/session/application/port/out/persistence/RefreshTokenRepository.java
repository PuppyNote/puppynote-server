package com.puppynoteserver.user.session.application.port.out.persistence;

import com.puppynoteserver.user.session.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    RefreshToken save(RefreshToken refreshToken);

    void deleteAllInBatch();
}
