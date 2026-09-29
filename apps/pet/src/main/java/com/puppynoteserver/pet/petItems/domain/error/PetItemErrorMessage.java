package com.puppynoteserver.pet.petItems.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetItemErrorMessage {

    PET_ITEM_NOT_FOUND("용품 정보를 찾을 수 없습니다.");

    private final String message;

}
