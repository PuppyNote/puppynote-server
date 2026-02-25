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

    private String breed;

    private LocalDate birthDate;

    private BigDecimal weight;

    private String profileImageUrl;

    @Builder
    private PetCreateRequest(String name, String breed, LocalDate birthDate, BigDecimal weight, String profileImageUrl) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.weight = weight;
        this.profileImageUrl = profileImageUrl;
    }

    public PetCreateServiceRequest toServiceRequest() {
        return PetCreateServiceRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
