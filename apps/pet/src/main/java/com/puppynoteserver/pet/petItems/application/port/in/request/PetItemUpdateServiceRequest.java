package com.puppynoteserver.pet.petItems.application.port.in.request;

import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;
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
