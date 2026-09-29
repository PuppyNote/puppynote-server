package com.puppynoteserver.pet.petTip.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petTip.application.port.in.PetTipFinder;
import com.puppynoteserver.pet.petTip.application.port.in.response.PetTipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-tips")
public class PetTipController {

    private final PetTipFinder petTipFinder;

    @GetMapping("/random")
    public ApiResponse<PetTipResponse> getRandomTip() {
        return ApiResponse.ok(petTipFinder.getRandomTip());
    }
}
