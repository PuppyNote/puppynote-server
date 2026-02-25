package com.puppynoteserver.pet.petItems.service;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;

import java.util.List;

public interface PetItemReadService {

    List<PetItemResponse> getItemsByPetId(Long petId, ItemCategory category);

    PetItemResponse getItemDetail(Long petItemId);
}
