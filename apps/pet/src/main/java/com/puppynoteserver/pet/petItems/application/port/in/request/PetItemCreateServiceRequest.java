package com.puppynoteserver.pet.petItems.application.port.in.request;

import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetItemCreateServiceRequest {

    private Long petId;
    private String name;
    private ItemCategory category;
    private int purchaseCycleDays;
    private String purchaseUrl;
    private String imageKey;

    public PetItem toEntity(Pet pet) {
        return PetItem.of(pet, name, category, purchaseCycleDays, purchaseUrl, imageKey);
    }
}
