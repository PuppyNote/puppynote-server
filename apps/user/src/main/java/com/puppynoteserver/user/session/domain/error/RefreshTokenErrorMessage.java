package com.puppynoteserver.user.session.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefreshTokenErrorMessage {

    UNKNOWN_TOKEN("유효하지 않은 RefreshToken입니다.");

    private final String message;

}
