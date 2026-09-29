package com.puppynoteserver.pet.pets.application.port.in;

import com.puppynoteserver.pet.pets.application.port.in.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.application.port.in.response.PetCreateResponse;

public interface PetRegister {

    PetCreateResponse createPet(PetCreateServiceRequest request);
}
