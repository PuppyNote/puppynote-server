package com.puppynoteserver.alertHistory.controller.alertHistory;

import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryReadService;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryResponse;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryStatusResponse;
import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.global.page.request.PageInfoRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alertHistories")
@RequiredArgsConstructor
public class AlertHistoryController {

	private final AlertHistoryService alertHistoryService;
	private final AlertHistoryReadService alertHistoryReadService;

	@GetMapping
	public ApiResponse<PageCustom<AlertHistoryResponse>> getAlertHistory(@ModelAttribute PageInfoRequest request) {
		return ApiResponse.ok(alertHistoryReadService.getAlertHistory(request.toServiceRequest()));
	}

	@PatchMapping("/{id}")
	public ApiResponse<AlertHistoryStatusResponse> updateAlertHistoryStatus(@PathVariable Long id) {
		return ApiResponse.ok(alertHistoryService.updateAlertHistoryStatus(id));
	}
}
