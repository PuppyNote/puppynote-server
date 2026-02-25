package com.puppynoteserver.pet.petItems.repository;

import com.puppynoteserver.pet.petItems.entity.PetItemPurchase;

import java.util.List;
import java.util.Optional;

public interface PetItemPurchaseRepository {

    PetItemPurchase save(PetItemPurchase petItemPurchase);

    List<PetItemPurchase> findLatestByPetItemIds(List<Long> petItemIds);

    Optional<PetItemPurchase> findLatestByPetItemId(Long petItemId);
}
