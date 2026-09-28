package com.puppynoteserver.pet.petWalkAlarms.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmStatus {

    YES("알람 활성화"),
    NO("알람 비활성화");

    private final String description;
}
