package com.puppynoteserver.alertSetting.controller;

import com.puppynoteserver.alertSetting.controller.request.AlertSettingUpdateRequest;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.alertSetting.service.AlertSettingService;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alert-setting")
public class AlertSettingController {

	private final AlertSettingService alertSettingService;
	private final AlertSettingReadService alertSettingReadService;

	@GetMapping
	public ApiResponse<AlertSettingResponse> getAlertSetting() {
		return ApiResponse.ok(alertSettingReadService.getAlertSetting());
	}

	@PatchMapping
	public ApiResponse<AlertSettingResponse> updateAlertSetting(
		@RequestBody @Valid AlertSettingUpdateRequest request) {
		return ApiResponse.ok(alertSettingService.updateAlertSetting(request.toServiceRequest()));
	}
}
