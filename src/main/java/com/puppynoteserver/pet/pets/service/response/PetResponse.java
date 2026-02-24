package com.puppynoteserver.pet.pets.service.response;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Getter;

@Getter
public class PetResponse {

    private final Long petId;
    private final String petName;

    private PetResponse(Long petId, String petName) {
        this.petId = petId;
        this.petName = petName;
    }

    public static PetResponse from(Pet pet) {
        return new PetResponse(pet.getId(), pet.getName());
    }
}
