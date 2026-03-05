package com.puppynoteserver.pet.petItemPurchase.service;

import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;

public interface PetItemPurchaseWriteService {

    PetItemPurchaseResponse recordPurchase(PetItemPurchaseCreateServiceRequest request);

    void deleteAllByPetItemId(Long petItemId);

    void deleteAllByPetId(Long petId);
}
