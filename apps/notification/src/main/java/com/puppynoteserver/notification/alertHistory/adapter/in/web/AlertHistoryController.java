package com.puppynoteserver.notification.alertHistory.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.global.page.request.PageInfoRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.notification.alertHistory.application.port.in.AlertHistoryFinder;
import com.puppynoteserver.notification.alertHistory.application.port.in.AlertHistoryUpdater;
import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryResponse;
import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alertHistories")
@RequiredArgsConstructor
public class AlertHistoryController {

	private final AlertHistoryUpdater alertHistoryUpdater;
	private final AlertHistoryFinder alertHistoryFinder;

	@GetMapping
	public ApiResponse<PageCustom<AlertHistoryResponse>> getAlertHistory(@ModelAttribute PageInfoRequest request) {
		return ApiResponse.ok(alertHistoryFinder.getAlertHistory(request.toServiceRequest()));
	}

	@GetMapping("/unchecked")
	public ApiResponse<Boolean> hasUncheckedAlerts() {
		return ApiResponse.ok(alertHistoryFinder.hasUncheckedAlerts());
	}

	@PatchMapping("/{id}")
	public ApiResponse<AlertHistoryStatusResponse> updateAlertHistoryStatus(@PathVariable Long id) {
		return ApiResponse.ok(alertHistoryUpdater.updateAlertHistoryStatus(id));
	}
}
