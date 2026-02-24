package com.puppynoteserver.walk.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalkStatus {

    COMPLETED("completed");

    private final String text;
}
