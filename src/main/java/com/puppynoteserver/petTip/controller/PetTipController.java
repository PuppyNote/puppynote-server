package com.puppynoteserver.petTip.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.petTip.service.PetTipReadService;
import com.puppynoteserver.petTip.service.response.PetTipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-tips")
public class PetTipController {

    private final PetTipReadService petTipReadService;

    @GetMapping("/random")
    public ApiResponse<PetTipResponse> getRandomTip() {
        return ApiResponse.ok(petTipReadService.getRandomTip());
    }
}
