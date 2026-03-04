package com.puppynoteserver.pet.pets.service.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetUpdateServiceRequest {

    private final String name;
    private final LocalDate birthDate;
    private final String profileImage;

    @Builder
    private PetUpdateServiceRequest(String name, LocalDate birthDate, String profileImage) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }
}
