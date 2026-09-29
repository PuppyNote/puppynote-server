package com.puppynoteserver.user.session.adapter.out.persistence;

import com.puppynoteserver.user.session.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUser_IdAndDeviceId(Long userId, String deviceId);
}
