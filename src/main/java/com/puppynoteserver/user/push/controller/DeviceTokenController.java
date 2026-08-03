package com.puppynoteserver.user.push.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.push.controller.request.DeviceTokenRegisterRequest;
import com.puppynoteserver.user.push.service.PushWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/device-tokens")
public class DeviceTokenController {

    private final PushWriteService pushWriteService;

    /**
     * 로그인 이후 푸시 디바이스 토큰을 등록/갱신한다.
     * deviceId 기준 upsert이므로 같은 요청을 여러 번 보내도 안전하다.
     */
    @PostMapping
    public ApiResponse<Void> registerDeviceToken(@Valid @RequestBody DeviceTokenRegisterRequest request) {
        pushWriteService.registerDeviceToken(request.toServiceRequest());
        return ApiResponse.ok(null);
    }
}
