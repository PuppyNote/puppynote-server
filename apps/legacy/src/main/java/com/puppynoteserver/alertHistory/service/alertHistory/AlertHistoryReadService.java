package com.puppynoteserver.alertHistory.service.alertHistory;

import com.puppynoteserver.alertHistory.entity.AlertHistory;
import com.puppynoteserver.alertHistory.repository.AlertHistoryRepository;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryResponse;
import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.global.page.response.PageableCustom;
import com.puppynoteserver.global.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlertHistoryReadService {

	private final AlertHistoryRepository alertHistoryRepository;
	private final SecurityService securityService;

	public AlertHistory findByOne(Long id) {
		return alertHistoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 알림 내역은 없습니다. id = " + id));
	}

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

	public boolean hasUncheckedAlerts() {
		long userId = securityService.getCurrentLoginUserInfo().getUserId();
		return alertHistoryRepository.hasUncheckedAlerts(userId);
	}

	public boolean hasFriendCode(Long userId, String friendCode) {
		return alertHistoryRepository.hasFriendCode(userId, friendCode);
	}

}
