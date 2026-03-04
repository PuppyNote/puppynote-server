package com.puppynoteserver.pet.petItemPurchase.service;

import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;

import java.util.List;

public interface PetItemPurchaseReadService {

    List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId);
}
