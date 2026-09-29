package com.puppynoteserver.pet.petItems.application.port.in;

import com.puppynoteserver.pet.petItems.application.port.in.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.application.port.in.response.PetItemResponse;

public interface PetItemUpdater {

    PetItemResponse update(Long petItemId, PetItemUpdateServiceRequest request);
}
