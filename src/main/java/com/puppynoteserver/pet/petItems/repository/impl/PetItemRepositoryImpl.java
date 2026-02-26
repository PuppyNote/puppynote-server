package com.puppynoteserver.pet.petItems.repository.impl;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.repository.PetItemJpaRepository;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetItemRepositoryImpl implements PetItemRepository {

    private final PetItemJpaRepository petItemJpaRepository;

    @Override
    public PetItem save(PetItem petItem) {
        return petItemJpaRepository.save(petItem);
    }

    @Override
    public Optional<PetItem> findById(Long id) {
        return petItemJpaRepository.findById(id);
    }

    @Override
    public List<PetItem> findByPetId(Long petId) {
        return petItemJpaRepository.findByPetId(petId);
    }

    @Override
    public List<PetItem> findByPetIdAndCategory(Long petId, ItemCategory category) {
        return petItemJpaRepository.findByPetIdAndCategory(petId, category);
    }
}
