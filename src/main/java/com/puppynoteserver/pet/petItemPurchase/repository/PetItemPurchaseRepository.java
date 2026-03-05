package com.puppynoteserver.pet.petItemPurchase.repository;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;

import java.util.List;
import java.util.Optional;

public interface PetItemPurchaseRepository {

    PetItemPurchase save(PetItemPurchase petItemPurchase);

    List<PetItemPurchase> findLatestByPetItemIds(List<Long> petItemIds);

    Optional<PetItemPurchase> findLatestByPetItemId(Long petItemId);

    List<PetItemPurchase> findAllByPetItemId(Long petItemId);

    List<PetItemPurchase> findAllLatestPurchases();

    List<PetItemPurchase> findLatestPurchasesByPetId(Long petId);

    void deleteAllByPetItemId(Long petItemId);
}
