package com.puppynoteserver.user.refreshToken.repository;

import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
