package com.puppynoteserver.healthRecord.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordType {

    WEIGHT("weight"),
    VACCINATION("vaccination"),
    MEDICATION("medication");

    private final String text;
}
