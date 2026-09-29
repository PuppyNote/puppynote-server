package com.puppynoteserver.pet.petItemPurchase.application.port.in;

import com.puppynoteserver.pet.petItemPurchase.application.port.in.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.response.PetItemPurchaseResponse;

public interface PetItemPurchaseRegister {

    PetItemPurchaseResponse recordPurchase(PetItemPurchaseCreateServiceRequest request);
}
