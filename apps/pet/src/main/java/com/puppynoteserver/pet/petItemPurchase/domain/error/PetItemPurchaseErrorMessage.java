package com.puppynoteserver.pet.petItemPurchase.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetItemPurchaseErrorMessage {

    PURCHASE_NOT_FOUND("구매 이력을 찾을 수 없습니다.");

    private final String message;

}
