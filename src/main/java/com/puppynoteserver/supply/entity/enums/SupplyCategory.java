package com.puppynoteserver.supply.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupplyCategory {

    FOOD("food"),
    TOY("toy"),
    HYGIENE("hygiene");

    private final String text;
}
