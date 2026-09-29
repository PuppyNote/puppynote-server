package com.puppynoteserver.user.user.application.port.out;

import com.puppynoteserver.user.user.domain.enums.SnsType;

/**
 * [APPLICATION · OUT PORT] 소셜 로그인 제공자로부터 이메일을 조회한다.
 *
 * <p>
 * 포트로 뽑아 두는 이유는 제공자가 계약이 아니기 때문이다 — Kakao/Google/Apple 구현체를
 * 늘리거나 바꿔도 {@code LoginService}는 {@link SnsType}별로 골라 쓰기만 하면 된다.
 */
public interface OAuthApiClient {

    SnsType oAuthSnsType();

    String getEmail(String code);
}
