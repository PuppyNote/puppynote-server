package com.puppynoteserver.pet.petItems.application.port.in;

import com.puppynoteserver.pet.petItems.application.port.in.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.application.port.in.response.PetItemResponse;

public interface PetItemRegister {

    PetItemResponse create(PetItemCreateServiceRequest request);
}
