package com.puppynoteserver.user.refreshToken.service.impl;

import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenRepository;
import com.puppynoteserver.user.refreshToken.service.RefreshTokenReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenReadServiceImpl implements RefreshTokenReadService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new PuppyNoteException("유효하지 않은 RefreshToken입니다."));
    }
}
