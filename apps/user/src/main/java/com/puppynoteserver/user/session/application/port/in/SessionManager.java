package com.puppynoteserver.user.session.application.port.in;

import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.user.session.application.port.in.request.TokenRefreshServiceRequest;
import com.puppynoteserver.user.session.application.port.in.response.TokenRefreshResponse;
import com.puppynoteserver.user.user.domain.entity.User;

public interface SessionManager {

    TokenRefreshResponse refresh(TokenRefreshServiceRequest request);

    /**
     * 로그인 시점에 디바이스 기준으로 RefreshToken을 upsert한다.
     * user 서브도메인(LoginService)이 로그인 후 호출한다.
     */
    void upsertByDeviceId(User user, JwtToken jwtToken, String deviceId);
}
