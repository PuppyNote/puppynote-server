package com.puppynoteserver.pet.petItems.adapter.out.persistence;

import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetItemJpaRepository extends JpaRepository<PetItem, Long> {

    List<PetItem> findByPetId(Long petId);

    List<PetItem> findByPetIdAndCategory(Long petId, ItemCategory category);

    long countByPetId(Long petId);

    void deleteAllByPetId(Long petId);
}
