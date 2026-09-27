package com.puppynoteserver.pet.petItemPurchase.service.request;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetItemPurchaseCreateServiceRequest {

    private final Long petItemId;
    private final LocalDate purchasedAt;

    @Builder
    private PetItemPurchaseCreateServiceRequest(Long petItemId, LocalDate purchasedAt) {
        this.petItemId = petItemId;
        this.purchasedAt = purchasedAt;
    }

    public PetItemPurchase toEntity(PetItem petItem) {
        return PetItemPurchase.of(petItem, purchasedAt);
    }
}
