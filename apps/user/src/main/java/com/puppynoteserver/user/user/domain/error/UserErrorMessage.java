package com.puppynoteserver.user.user.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorMessage {

    UNKNOWN_USER("해당 회원은 존재하지 않습니다."),
    EXIST_EMAIL("이미 사용 중인 이메일입니다."),
    UNKNOWN_EMAIL("존재하지 않는 이메일입니다.");

    private final String message;

}
