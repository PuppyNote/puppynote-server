package com.puppynoteserver.pet.pets.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetErrorMessage {

    PET_NOT_FOUND("펫을 찾을 수 없습니다."),
    FAMILY_MEMBER_NOT_FOUND("해당 펫의 가족 구성원 정보를 찾을 수 없습니다."),
    DELETE_NOT_ALLOWED("펫 삭제는 OWNER만 가능합니다.");

    private final String message;

}
