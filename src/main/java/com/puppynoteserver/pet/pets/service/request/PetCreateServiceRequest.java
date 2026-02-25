package com.puppynoteserver.pet.pets.service.request;

import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Builder;

import java.time.LocalDate;

public class PetCreateServiceRequest {

    private final String name;
    private final LocalDate birthDate;
    private final String profileImageUrl;

    @Builder
    private PetCreateServiceRequest(String name, LocalDate birthDate, String profileImageUrl) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
    }

    public Pet toEntity() {
        return Pet.of(name, birthDate, profileImageUrl);
    }
}
