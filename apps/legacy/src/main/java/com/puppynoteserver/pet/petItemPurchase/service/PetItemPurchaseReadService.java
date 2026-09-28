package com.puppynoteserver.pet.petItemPurchase.service;

import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PetItemPurchaseReadService {

    List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId);

    Map<Long, LocalDate> findLatestPurchaseDatesByPetItemIds(List<Long> petItemIds);

    Optional<LocalDate> findLatestPurchaseDateByPetItemId(Long petItemId);
}
