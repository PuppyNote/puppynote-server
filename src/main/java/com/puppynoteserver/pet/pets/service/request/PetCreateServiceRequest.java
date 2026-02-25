package com.puppynoteserver.pet.pets.service.request;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Builder;

import java.time.LocalDate;

public class PetCreateServiceRequest {

    private final String name;
    private final LocalDate birthDate;
    private final String profileImage;

    @Builder
    private PetCreateServiceRequest(String name, LocalDate birthDate, String profileImage) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }

    public Pet toEntity() {
        return Pet.of(name, birthDate, profileImage);
    }
}
