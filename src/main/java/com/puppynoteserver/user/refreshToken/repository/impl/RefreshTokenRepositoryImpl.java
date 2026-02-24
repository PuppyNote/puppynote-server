package com.puppynoteserver.user.refreshToken.repository.impl;

import org.springframework.stereotype.Repository;

import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenJpaRepository;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

	private final RefreshTokenJpaRepository refreshTokenJpaRepository;

	@Override
	public void deleteAllInBatch() {
		refreshTokenJpaRepository.deleteAllInBatch();
	}
}
