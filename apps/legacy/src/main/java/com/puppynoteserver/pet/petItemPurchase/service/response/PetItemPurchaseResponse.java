package com.puppynoteserver.pet.petItemPurchase.service.response;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetItemPurchaseResponse {

    private final Long id;
    private final Long petItemId;
    private final LocalDate purchasedAt;

    private PetItemPurchaseResponse(Long id, Long petItemId, LocalDate purchasedAt) {
        this.id = id;
        this.petItemId = petItemId;
        this.purchasedAt = purchasedAt;
    }

    public static PetItemPurchaseResponse of(PetItemPurchase purchase) {
        return new PetItemPurchaseResponse(
                purchase.getId(),
                purchase.getPetItem().getId(),
                purchase.getPurchasedAt()
        );
    }
}
