package com.puppynoteserver.pet.petItems.application.port.out.persistence;

import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;

import java.util.List;
import java.util.Optional;

public interface PetItemRepository {

    PetItem save(PetItem petItem);

    Optional<PetItem> findById(Long id);

    List<PetItem> findByPetId(Long petId);

    List<PetItem> findByPetIdAndCategory(Long petId, ItemCategory category);

    long countByPetId(Long petId);

    void deleteById(Long id);

    void deleteAllByPetId(Long petId);
}
