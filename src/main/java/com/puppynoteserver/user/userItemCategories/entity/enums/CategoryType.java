package com.puppynoteserver.user.userItemCategories.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    ITEM("용품"),
    ACTIVITY("활동");

    private final String description;
}
