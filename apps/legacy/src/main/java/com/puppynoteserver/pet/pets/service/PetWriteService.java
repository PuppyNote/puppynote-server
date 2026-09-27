package com.puppynoteserver.pet.pets.service;

import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.request.PetUpdateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;

public interface PetWriteService {

    PetCreateResponse createPet(PetCreateServiceRequest request);

    void updatePet(Long petId, PetUpdateServiceRequest request);

    void deletePet(Long petId);
}
