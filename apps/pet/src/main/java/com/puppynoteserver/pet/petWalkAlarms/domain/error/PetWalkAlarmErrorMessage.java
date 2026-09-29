package com.puppynoteserver.pet.petWalkAlarms.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetWalkAlarmErrorMessage {

    ALARM_NOT_FOUND("알람을 찾을 수 없습니다.");

    private final String message;

}
