package com.puppynoteserver.home.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.home.service.response.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeReadService homeReadService;

    @GetMapping
    public ApiResponse<HomeResponse> getHomeInfo(@RequestParam Long petId) {
        return ApiResponse.ok(homeReadService.getHomeInfo(petId));
    }
}
