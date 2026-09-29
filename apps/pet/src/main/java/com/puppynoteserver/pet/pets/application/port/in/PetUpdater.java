package com.puppynoteserver.pet.pets.application.port.in;

import com.puppynoteserver.pet.pets.application.port.in.request.PetUpdateServiceRequest;

public interface PetUpdater {

    void updatePet(Long petId, PetUpdateServiceRequest request);
}
