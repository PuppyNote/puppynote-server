package com.puppynoteserver.pet.petItems.repository;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetItemJpaRepository extends JpaRepository<PetItem, Long> {

    List<PetItem> findByPetId(Long petId);

    List<PetItem> findByPetIdAndCategory(Long petId, ItemCategory category);
}
