package com.puppynoteserver.pet.petItems.service;

import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;

public interface PetItemWriteService {

    PetItemResponse create(PetItemCreateServiceRequest request);

    PetItemResponse update(Long petItemId, PetItemUpdateServiceRequest request);

    void delete(Long petItemId);
}
