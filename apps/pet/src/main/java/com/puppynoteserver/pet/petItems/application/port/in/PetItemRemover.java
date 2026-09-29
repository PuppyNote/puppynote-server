package com.puppynoteserver.pet.petItems.application.port.in;

public interface PetItemRemover {

    void delete(Long petItemId);

    void deleteAllByPetId(Long petId);
}
