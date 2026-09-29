package com.puppynoteserver.notification.alertSetting.adapter.in.web;

import com.puppynoteserver.notification.alertSetting.adapter.in.web.request.AlertSettingUpdateRequest;
import com.puppynoteserver.notification.alertSetting.application.port.in.AlertSettingFinder;
import com.puppynoteserver.notification.alertSetting.application.port.in.AlertSettingUpdater;
import com.puppynoteserver.notification.alertSetting.application.port.in.response.AlertSettingResponse;
import com.puppynoteserver.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alert-setting")
public class AlertSettingController {

	private final AlertSettingUpdater alertSettingUpdater;
	private final AlertSettingFinder alertSettingFinder;

	@GetMapping
	public ApiResponse<AlertSettingResponse> getAlertSetting() {
		return ApiResponse.ok(alertSettingFinder.getAlertSetting());
	}

	@PatchMapping
	public ApiResponse<AlertSettingResponse> updateAlertSetting(
		@RequestBody @Valid AlertSettingUpdateRequest request) {
		return ApiResponse.ok(alertSettingUpdater.updateAlertSetting(request.toServiceRequest()));
	}
}
