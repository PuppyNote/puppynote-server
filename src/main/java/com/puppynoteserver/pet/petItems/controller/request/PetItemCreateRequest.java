package com.puppynoteserver.pet.petItems.controller.request;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetItemCreateRequest {

    @NotNull(message = "펫 ID는 필수입니다.")
    private Long petId;

    @NotBlank(message = "용품명은 필수입니다.")
    private String name;

    @NotNull(message = "카테고리는 필수입니다.")
    private ItemCategory category;

    @Min(value = 1, message = "구매 주기는 1일 이상이어야 합니다.")
    private int purchaseCycleDays;

    private String purchaseUrl;

    private String imageKey;

    public PetItemCreateServiceRequest toServiceRequest() {
        return PetItemCreateServiceRequest.builder()
                .petId(petId)
                .name(name)
                .category(category)
                .purchaseCycleDays(purchaseCycleDays)
                .purchaseUrl(purchaseUrl)
                .imageKey(imageKey)
                .build();
    }
}
