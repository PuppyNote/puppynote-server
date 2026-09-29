package com.puppynoteserver.pet.petItems.application.port.in;

import com.puppynoteserver.pet.petItems.application.port.in.response.PetItemResponse;
import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;

import java.util.List;

public interface PetItemFinder {

    List<PetItemResponse> getItemsByPetId(Long petId, ItemCategory category);

    PetItemResponse getItemDetail(Long petItemId);

    long countItemsByPetId(Long petId);

    PetItem findById(Long petItemId);
}
