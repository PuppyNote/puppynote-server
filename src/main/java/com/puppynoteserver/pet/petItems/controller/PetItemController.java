package com.puppynoteserver.pet.petItems.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petItems.controller.request.PetItemCreateRequest;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petItems.service.response.ItemCategoryGroupResponse;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-items")
public class PetItemController {

    private final PetItemWriteService petItemWriteService;
    private final PetItemReadService petItemReadService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetItemResponse> createPetItem(@Valid @RequestBody PetItemCreateRequest request) {
        return ApiResponse.created(petItemWriteService.create(request.toServiceRequest()));
    }

    @GetMapping
    public ApiResponse<List<PetItemResponse>> getPetItems(
            @RequestParam Long petId,
            @RequestParam(required = false) ItemCategory category) {
        return ApiResponse.ok(petItemReadService.getItemsByPetId(petId, category));
    }

    @GetMapping("/{petItemId}")
    public ApiResponse<PetItemResponse> getPetItemDetail(@PathVariable Long petItemId) {
        return ApiResponse.ok(petItemReadService.getItemDetail(petItemId));
    }

    @GetMapping("/categories")
    public ApiResponse<List<ItemCategoryGroupResponse>> getCategories() {
        return ApiResponse.ok(ItemCategoryGroupResponse.ofAll());
    }
}
