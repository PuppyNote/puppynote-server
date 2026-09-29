package com.puppynoteserver.pet.petItems.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petItems.adapter.in.web.request.PetItemCreateRequest;
import com.puppynoteserver.pet.petItems.adapter.in.web.request.PetItemUpdateRequest;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemFinder;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemRegister;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemRemover;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemUpdater;
import com.puppynoteserver.pet.petItems.application.port.in.response.ItemCategoryGroupResponse;
import com.puppynoteserver.pet.petItems.application.port.in.response.PetItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-items")
public class PetItemController {

    private final PetItemRegister petItemRegister;
    private final PetItemFinder petItemFinder;
    private final PetItemUpdater petItemUpdater;
    private final PetItemRemover petItemRemover;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetItemResponse> createPetItem(@Valid @RequestBody PetItemCreateRequest request) {
        return ApiResponse.created(petItemRegister.create(request.toServiceRequest()));
    }

    @GetMapping
    public ApiResponse<List<PetItemResponse>> getPetItems(
            @RequestParam Long petId,
            @RequestParam(required = false) ItemCategory category) {
        return ApiResponse.ok(petItemFinder.getItemsByPetId(petId, category));
    }

    @GetMapping("/{petItemId}")
    public ApiResponse<PetItemResponse> getPetItemDetail(@PathVariable Long petItemId) {
        return ApiResponse.ok(petItemFinder.getItemDetail(petItemId));
    }

    @GetMapping("/categories")
    public ApiResponse<List<ItemCategoryGroupResponse>> getCategories() {
        return ApiResponse.ok(ItemCategoryGroupResponse.ofAll());
    }

    @PatchMapping("/{petItemId}")
    public ApiResponse<PetItemResponse> updatePetItem(
            @PathVariable Long petItemId,
            @Valid @RequestBody PetItemUpdateRequest request) {
        return ApiResponse.ok(petItemUpdater.update(petItemId, request.toServiceRequest()));
    }

    @DeleteMapping("/{petItemId}")
    public ApiResponse<Void> deletePetItem(@PathVariable Long petItemId) {
        petItemRemover.delete(petItemId);
        return ApiResponse.ok(null);
    }
}
