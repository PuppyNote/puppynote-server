package com.puppynoteserver.pet.petItemPurchase.adapter.out.persistence;

import com.puppynoteserver.pet.petItemPurchase.application.port.out.persistence.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.domain.entity.PetItemPurchase;
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
    public List<PetItemPurchase> findAllByPetItemId(Long petItemId) {
        return petItemPurchaseJpaRepository.findAllByPetItemIdOrderByPurchasedAtDesc(petItemId);
    }

    @Override
    public List<PetItemPurchase> findAllLatestPurchases() {
        return petItemPurchaseJpaRepository.findAllLatestPurchases();
    }

    @Override
    public List<PetItemPurchase> findLatestPurchasesByPetId(Long petId) {
        return petItemPurchaseJpaRepository.findLatestPurchasesByPetId(petId);
    }

    @Override
    public void deleteAllByPetItemId(Long petItemId) {
        petItemPurchaseJpaRepository.deleteAllByPetItemId(petItemId);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        petItemPurchaseJpaRepository.deleteAllByPetItemPetId(petId);
    }

    @Override
    public Optional<PetItemPurchase> findById(Long id) {
        return petItemPurchaseJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        petItemPurchaseJpaRepository.deleteById(id);
    }
}
