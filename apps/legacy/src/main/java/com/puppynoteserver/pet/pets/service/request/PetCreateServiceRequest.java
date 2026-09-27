package com.puppynoteserver.pet.pets.service.request;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Builder;

import java.time.LocalDate;

public class PetCreateServiceRequest {

    private final String name;
    private final LocalDate birthDate;
    private final String profileImage;
    private final String registrationNumber;

    @Builder
    private PetCreateServiceRequest(String name, LocalDate birthDate, String profileImage, String registrationNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.registrationNumber = registrationNumber;
    }

    public Pet toEntity() {
        return Pet.of(name, birthDate, profileImage, registrationNumber);
    }
}
