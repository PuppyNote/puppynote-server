package com.puppynoteserver.pet.pets.service.response;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Getter;

@Getter
public class PetResponse {

    private final Long petId;
    private final String petName;
    private final String petProfileUrl;

    private PetResponse(Long petId, String petName, String petProfileUrl) {
        this.petId = petId;
        this.petName = petName;
        this.petProfileUrl = petProfileUrl;
    }

    public static PetResponse of(Pet pet, String petProfileUrl) {
        return new PetResponse(pet.getId(), pet.getName(), petProfileUrl);
    }
}
