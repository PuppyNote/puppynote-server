package com.puppynoteserver.pet.petTip.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetTipErrorMessage {

    TIP_NOT_FOUND("등록된 팁이 없습니다.");

    private final String message;

}
