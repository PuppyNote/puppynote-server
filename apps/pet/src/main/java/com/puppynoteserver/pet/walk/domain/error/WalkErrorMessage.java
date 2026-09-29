package com.puppynoteserver.pet.walk.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalkErrorMessage {

    WALK_NOT_FOUND("산책 기록을 찾을 수 없습니다.");

    private final String message;

}
