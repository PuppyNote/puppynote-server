package com.puppynoteserver.pet.pets.service.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetUpdateServiceRequest {

    private final String name;
    private final LocalDate birthDate;
    private final String profileImage;
    private final String registrationNumber;

    @Builder
    private PetUpdateServiceRequest(String name, LocalDate birthDate, String profileImage, String registrationNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.registrationNumber = registrationNumber;
    }
}
