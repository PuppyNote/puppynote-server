package com.puppynoteserver.pet.petItemPurchase.application.port.in;

public interface PetItemPurchaseRemover {

    void deletePurchase(Long purchaseId);

    void deleteAllByPetItemId(Long petItemId);

    void deleteAllByPetId(Long petId);
}
