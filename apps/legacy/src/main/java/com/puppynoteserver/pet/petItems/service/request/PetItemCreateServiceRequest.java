package com.puppynoteserver.pet.petItems.service.request;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.pets.entity.Pet;
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
