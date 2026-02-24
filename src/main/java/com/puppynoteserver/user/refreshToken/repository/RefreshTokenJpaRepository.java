package com.puppynoteserver.user.refreshToken.repository;

import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
}
