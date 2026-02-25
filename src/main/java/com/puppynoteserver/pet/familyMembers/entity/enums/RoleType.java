package com.puppynoteserver.pet.familyMembers.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    OWNER("주인"),
    FAMILY("가족");

    private final String description;
}
