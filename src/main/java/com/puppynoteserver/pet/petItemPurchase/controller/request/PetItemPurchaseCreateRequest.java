package com.puppynoteserver.pet.petItemPurchase.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PetItemPurchaseCreateRequest {

    // 미입력 시 오늘 날짜로 처리
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchasedAt;

    public PetItemPurchaseCreateServiceRequest toServiceRequest(Long petItemId) {
        return PetItemPurchaseCreateServiceRequest.builder()
                .petItemId(petItemId)
                .purchasedAt(purchasedAt != null ? purchasedAt : LocalDate.now())
                .build();
    }
}
