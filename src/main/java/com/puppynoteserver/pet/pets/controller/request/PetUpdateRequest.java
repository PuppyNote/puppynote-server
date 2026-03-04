package com.puppynoteserver.pet.pets.controller.request;

import com.puppynoteserver.pet.pets.service.request.PetUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PetUpdateRequest {

    @NotBlank(message = "펫 이름은 필수입니다.")
    private String name;

    private LocalDate birthDate;

    private String profileImage;

    @Builder
    private PetUpdateRequest(String name, LocalDate birthDate, String profileImage) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }

    public PetUpdateServiceRequest toServiceRequest() {
        return PetUpdateServiceRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .profileImage(profileImage)
                .build();
    }
}
