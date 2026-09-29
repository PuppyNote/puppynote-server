package com.puppynoteserver.pet.petItemPurchase.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petItemPurchase.adapter.in.web.request.PetItemPurchaseCreateRequest;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseFinder;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseRegister;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseRemover;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.response.PetItemPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-items")
public class PetItemPurchaseController {

    private final PetItemPurchaseRegister petItemPurchaseRegister;
    private final PetItemPurchaseFinder petItemPurchaseFinder;
    private final PetItemPurchaseRemover petItemPurchaseRemover;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{petItemId}/purchases")
    public ApiResponse<PetItemPurchaseResponse> recordPurchase(
            @PathVariable Long petItemId,
            @RequestBody(required = false) PetItemPurchaseCreateRequest request) {
        PetItemPurchaseCreateRequest req = request != null ? request : new PetItemPurchaseCreateRequest();
        return ApiResponse.created(petItemPurchaseRegister.recordPurchase(req.toServiceRequest(petItemId)));
    }

    @GetMapping("/{petItemId}/purchases")
    public ApiResponse<List<PetItemPurchaseResponse>> getPurchaseHistory(@PathVariable Long petItemId) {
        return ApiResponse.ok(petItemPurchaseFinder.getPurchaseHistory(petItemId));
    }

    @DeleteMapping("/purchases/{purchaseId}")
    public ApiResponse<Void> deletePurchase(@PathVariable Long purchaseId) {
        petItemPurchaseRemover.deletePurchase(purchaseId);
        return ApiResponse.ok(null);
    }
}
