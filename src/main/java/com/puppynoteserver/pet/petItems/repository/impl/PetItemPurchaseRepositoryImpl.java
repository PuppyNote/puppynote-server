package com.puppynoteserver.pet.petItems.repository.impl;

import com.puppynoteserver.pet.petItems.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItems.repository.PetItemPurchaseJpaRepository;
import com.puppynoteserver.pet.petItems.repository.PetItemPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetItemPurchaseRepositoryImpl implements PetItemPurchaseRepository {

    private final PetItemPurchaseJpaRepository petItemPurchaseJpaRepository;

    @Override
    public PetItemPurchase save(PetItemPurchase petItemPurchase) {
        return petItemPurchaseJpaRepository.save(petItemPurchase);
    }

    @Override
    public List<PetItemPurchase> findLatestByPetItemIds(List<Long> petItemIds) {
        if (petItemIds.isEmpty()) {
            return List.of();
        }
        return petItemPurchaseJpaRepository.findLatestByPetItemIds(petItemIds);
    }

    @Override
    public Optional<PetItemPurchase> findLatestByPetItemId(Long petItemId) {
        return petItemPurchaseJpaRepository.findTopByPetItemIdOrderByPurchasedAtDesc(petItemId);
    }
}
