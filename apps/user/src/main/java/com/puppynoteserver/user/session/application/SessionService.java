package com.puppynoteserver.user.session.application;

import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.jwt.JwtTokenGenerator;
import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.user.session.application.port.in.SessionManager;
import com.puppynoteserver.user.session.application.port.in.request.TokenRefreshServiceRequest;
import com.puppynoteserver.user.session.application.port.in.response.TokenRefreshResponse;
import com.puppynoteserver.user.session.application.port.out.persistence.RefreshTokenRepository;
import com.puppynoteserver.user.session.domain.entity.RefreshToken;
import com.puppynoteserver.user.session.domain.error.RefreshTokenErrorMessage;
import com.puppynoteserver.user.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService implements SessionManager {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    public TokenRefreshResponse refresh(TokenRefreshServiceRequest request) {
        // 1. DB에 저장된 refreshToken인지 검증
        RefreshToken storedToken = validateRefreshToken(request.getRefreshToken());

        // 2. JWT 서명 및 만료 검증 후 새 토큰 발급
        JwtToken jwtToken = jwtTokenGenerator.generateJwtToken(request.getRefreshToken());

        // 3. DB의 refreshToken을 새 토큰으로 업데이트 (토큰 로테이션)
        storedToken.updateRefreshToken(jwtToken.getRefreshToken());

        return TokenRefreshResponse.from(jwtToken);
    }

    @Override
    public void upsertByDeviceId(User user, JwtToken jwtToken, String deviceId) {
        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresentOrElse(
                        existing -> existing.updateRefreshToken(jwtToken.getRefreshToken()),
                        () -> refreshTokenRepository.save(RefreshToken.of(user, jwtToken.getRefreshToken(), deviceId))
                );
    }

    private RefreshToken validateRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new PuppyNoteException(RefreshTokenErrorMessage.UNKNOWN_TOKEN.getMessage()));
    }
}
