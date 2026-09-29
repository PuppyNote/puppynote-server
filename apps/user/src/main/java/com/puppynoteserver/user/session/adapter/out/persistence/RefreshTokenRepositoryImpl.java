package com.puppynoteserver.user.session.adapter.out.persistence;

import com.puppynoteserver.user.session.application.port.out.persistence.RefreshTokenRepository;
import com.puppynoteserver.user.session.domain.entity.RefreshToken;
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
	public Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId) {
		return refreshTokenJpaRepository.findByUser_IdAndDeviceId(userId, deviceId);
	}

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		return refreshTokenJpaRepository.save(refreshToken);
	}

	@Override
	public void deleteAllInBatch() {
		refreshTokenJpaRepository.deleteAllInBatch();
	}
}
