package com.puppynoteserver.pet.petItemPurchase.repository.impl;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseJpaRepository;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
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

    @Override
    public List<PetItemPurchase> findAllLatestPurchases() {
        return petItemPurchaseJpaRepository.findAllLatestPurchases();
    }

    @Override
    public List<PetItemPurchase> findLatestPurchasesByPetId(Long petId) {
        return petItemPurchaseJpaRepository.findLatestPurchasesByPetId(petId);
    }
}
