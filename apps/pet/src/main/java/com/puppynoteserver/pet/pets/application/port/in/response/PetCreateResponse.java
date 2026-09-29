package com.puppynoteserver.pet.pets.application.port.in.response;

import com.puppynoteserver.pet.pets.domain.entity.Pet;
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
