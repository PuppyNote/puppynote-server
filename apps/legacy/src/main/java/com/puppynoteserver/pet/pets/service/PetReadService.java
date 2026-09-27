package com.puppynoteserver.pet.pets.service;

import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.response.PetResponse;

import java.util.List;

public interface PetReadService {

    List<PetResponse> getMyPets();

    Pet findById(Long petId);
}
