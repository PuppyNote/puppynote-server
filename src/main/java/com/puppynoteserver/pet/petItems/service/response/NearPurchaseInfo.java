package com.puppynoteserver.pet.petItems.service.response;

import lombok.Getter;

@Getter
public class NearPurchaseInfo {

    private final int count;
    private final String nearestItemName;

    private NearPurchaseInfo(int count, String nearestItemName) {
        this.count = count;
        this.nearestItemName = nearestItemName;
    }

    public static NearPurchaseInfo of(int count, String nearestItemName) {
        return new NearPurchaseInfo(count, nearestItemName);
    }
}
