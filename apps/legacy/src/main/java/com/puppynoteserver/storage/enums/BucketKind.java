package com.puppynoteserver.storage.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BucketKind {

    PUPPY_PROFILE("puppy-profile"),
    WALK_PHOTO("puppy-walk"),
    PET_ITEM_PHOTO("puppy-item"),
    USER_PROFILE("user-profile"),
    COMMUNITY_POST("community");

    private final String folder;

}
