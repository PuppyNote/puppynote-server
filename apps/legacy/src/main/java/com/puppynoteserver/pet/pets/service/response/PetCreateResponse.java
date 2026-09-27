package com.puppynoteserver.pet.pets.service.response;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Getter;

@Getter
public class PetCreateResponse {

    private final Long petId;
    private final String petName;

    private PetCreateResponse(Long petId, String petName) {
        this.petId = petId;
        this.petName = petName;
    }

    public static PetCreateResponse from(Pet pet) {
        return new PetCreateResponse(pet.getId(), pet.getName());
    }
}
