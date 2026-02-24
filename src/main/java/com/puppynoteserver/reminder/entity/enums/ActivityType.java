package com.puppynoteserver.reminder.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {

    WALK("walk"),
    FEEDING("feeding");

    private final String text;
}
