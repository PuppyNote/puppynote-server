package com.puppynoteserver.pet.petItemPurchase.application.port.in;

import com.puppynoteserver.pet.petItemPurchase.application.port.in.response.PetItemPurchaseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PetItemPurchaseFinder {

    List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId);

    Map<Long, LocalDate> findLatestPurchaseDatesByPetItemIds(List<Long> petItemIds);

    Optional<LocalDate> findLatestPurchaseDateByPetItemId(Long petItemId);
}
