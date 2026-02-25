package com.puppynoteserver.pet.pets.controller.request;

import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PetCreateRequest {

    @NotBlank(message = "펫 이름은 필수입니다.")
    private String name;

    private LocalDate birthDate;

    private String profileImage;

    @Builder
    private PetCreateRequest(String name, LocalDate birthDate,  String profileImage) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }

    public PetCreateServiceRequest toServiceRequest() {
        return PetCreateServiceRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .profileImage(profileImage)
                .build();
    }
}
