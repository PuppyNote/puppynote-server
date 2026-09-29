package com.puppynoteserver.pet.pets.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.pets.adapter.in.web.request.PetCreateRequest;
import com.puppynoteserver.pet.pets.adapter.in.web.request.PetUpdateRequest;
import com.puppynoteserver.pet.pets.application.port.in.PetFinder;
import com.puppynoteserver.pet.pets.application.port.in.PetRegister;
import com.puppynoteserver.pet.pets.application.port.in.PetRemover;
import com.puppynoteserver.pet.pets.application.port.in.PetUpdater;
import com.puppynoteserver.pet.pets.application.port.in.response.PetCreateResponse;
import com.puppynoteserver.pet.pets.application.port.in.response.PetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class PetController {

    private final PetFinder petFinder;
    private final PetRegister petRegister;
    private final PetUpdater petUpdater;
    private final PetRemover petRemover;

    @GetMapping
    public ApiResponse<List<PetResponse>> getMyPets() {
        return ApiResponse.ok(petFinder.getMyPets());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetCreateResponse> createPet(@Valid @RequestBody PetCreateRequest request) {
        return ApiResponse.created(petRegister.createPet(request.toServiceRequest()));
    }

    @PatchMapping("/{petId}")
    public ApiResponse<Void> updatePet(@PathVariable Long petId, @Valid @RequestBody PetUpdateRequest request) {
        petUpdater.updatePet(petId, request.toServiceRequest());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{petId}")
    public ApiResponse<Void> deletePet(@PathVariable Long petId) {
        petRemover.deletePet(petId);
        return ApiResponse.ok(null);
    }
}
