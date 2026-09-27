package com.puppynoteserver.appVersion.controller;

import com.puppynoteserver.appVersion.service.AppVersionService;
import com.puppynoteserver.appVersion.service.response.AppVersionResponse;
import com.puppynoteserver.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app-version")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping
    public ApiResponse<AppVersionResponse> getAppVersion() {
        return ApiResponse.ok(appVersionService.getAppVersion());
    }
}
