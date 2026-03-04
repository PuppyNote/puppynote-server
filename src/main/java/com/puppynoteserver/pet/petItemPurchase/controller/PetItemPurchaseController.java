package com.puppynoteserver.pet.petItemPurchase.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petItemPurchase.controller.request.PetItemPurchaseCreateRequest;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-items")
public class PetItemPurchaseController {

    private final PetItemPurchaseWriteService petItemPurchaseWriteService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{petItemId}/purchases")
    public ApiResponse<PetItemPurchaseResponse> recordPurchase(
            @PathVariable Long petItemId,
            @RequestBody(required = false) PetItemPurchaseCreateRequest request) {
        PetItemPurchaseCreateRequest req = request != null ? request : new PetItemPurchaseCreateRequest();
        return ApiResponse.created(petItemPurchaseWriteService.recordPurchase(req.toServiceRequest(petItemId)));
    }
}
