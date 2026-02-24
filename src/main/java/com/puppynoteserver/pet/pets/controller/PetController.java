package com.puppynoteserver.pet.pets.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class PetController {

    private final PetReadService petReadService;

    @GetMapping
    public ApiResponse<List<PetResponse>> getMyPets() {
        return ApiResponse.ok(petReadService.getMyPets());
    }
}
