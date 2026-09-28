package com.puppynoteserver.pet.familyMembers.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyMemberStatus {

    PENDING("초대 대기"),
    DONE("등록 완료");

    private final String description;
}
