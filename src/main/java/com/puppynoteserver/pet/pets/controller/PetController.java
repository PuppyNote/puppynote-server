package com.puppynoteserver.pet.pets.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.pets.controller.request.PetCreateRequest;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class PetController {

    private final PetReadService petReadService;
    private final PetWriteService petWriteService;

    @GetMapping
    public ApiResponse<List<PetResponse>> getMyPets() {
        return ApiResponse.ok(petReadService.getMyPets());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetCreateResponse> createPet(@Valid @RequestBody PetCreateRequest request) {
        return ApiResponse.created(petWriteService.createPet(request.toServiceRequest()));
    }
}
