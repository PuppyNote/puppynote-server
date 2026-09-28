package com.puppynoteserver.pet.petItems.service.request;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetItemUpdateServiceRequest {

    private String name;
    private ItemCategory category;
    private int purchaseCycleDays;
    private String purchaseUrl;
    private String imageKey;
}
