package com.puppynoteserver.notification.alertHistory.application;

import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.global.page.response.PageableCustom;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.notification.alertHistory.application.port.in.AlertHistoryFinder;
import com.puppynoteserver.notification.alertHistory.application.port.in.AlertHistoryRegister;
import com.puppynoteserver.notification.alertHistory.application.port.in.AlertHistoryUpdater;
import com.puppynoteserver.notification.alertHistory.application.port.in.request.AlertHistoryServiceRequest;
import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryResponse;
import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryStatusResponse;
import com.puppynoteserver.notification.alertHistory.application.port.out.persistence.AlertHistoryRepository;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistory;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlertHistoryService implements AlertHistoryFinder, AlertHistoryRegister, AlertHistoryUpdater {

	private final AlertHistoryRepository alertHistoryRepository;
	private final SecurityService securityService;

	@Override
	public AlertHistory findByOne(Long id) {
		return alertHistoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 알림 내역은 없습니다. id = " + id));
	}

	@Override
	public PageCustom<AlertHistoryResponse> getAlertHistory(PageInfoServiceRequest request) {
		long userId = securityService.getCurrentLoginUserInfo().getUserId();

		Page<AlertHistory> alertHistoryPage = alertHistoryRepository.findByUserId(
			userId,
			request.toPageable()
		);

		List<AlertHistoryResponse> alertHistoryResponseList = alertHistoryPage.getContent().stream()
			.map(AlertHistoryResponse::of)
			.toList();

		return PageCustom.<AlertHistoryResponse>builder()
			.content(alertHistoryResponseList)
			.pageInfo(PageableCustom.of(alertHistoryPage))
			.build();
	}

	@Override
	public boolean hasUncheckedAlerts() {
		long userId = securityService.getCurrentLoginUserInfo().getUserId();
		return alertHistoryRepository.hasUncheckedAlerts(userId);
	}

	@Override
	public boolean hasFriendCode(Long userId, String friendCode) {
		return alertHistoryRepository.hasFriendCode(userId, friendCode);
	}

	@Override
	public AlertHistory createAlertHistory(AlertHistoryServiceRequest alertHistoryServiceRequest) {
		return alertHistoryRepository.save(alertHistoryServiceRequest.toEntity());
	}

	@Override
	public AlertHistoryStatusResponse updateAlertHistoryStatus(Long id) {
		AlertHistory alertHistory = findByOne(id);
		alertHistory.updateAlertHistoryStatus(AlertHistoryStatus.CHECKED);

		return AlertHistoryStatusResponse.of(alertHistory.getAlertHistoryStatus());
	}
}
