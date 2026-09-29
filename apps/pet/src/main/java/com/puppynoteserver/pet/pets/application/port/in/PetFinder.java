package com.puppynoteserver.pet.pets.application.port.in;

import com.puppynoteserver.pet.pets.application.port.in.response.PetResponse;
import com.puppynoteserver.pet.pets.domain.entity.Pet;

import java.util.List;

public interface PetFinder {

    List<PetResponse> getMyPets();

    Pet findById(Long petId);
}
