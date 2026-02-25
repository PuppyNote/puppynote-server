package com.puppynoteserver.pet.petItems.repository;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;

import java.util.List;
import java.util.Optional;

public interface PetItemRepository {

    PetItem save(PetItem petItem);

    Optional<PetItem> findById(Long id);

    List<PetItem> findByPetId(Long petId);

    List<PetItem> findByPetIdAndCategory(Long petId, ItemCategory category);
}
