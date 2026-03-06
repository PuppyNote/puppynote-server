package com.puppynoteserver.user.refreshToken.repository.impl;

import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenJpaRepository;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

	private final RefreshTokenJpaRepository refreshTokenJpaRepository;

	@Override
	public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
		return refreshTokenJpaRepository.findByRefreshToken(refreshToken);
	}

	@Override
	public void deleteAllInBatch() {
		refreshTokenJpaRepository.deleteAllInBatch();
	}
}
