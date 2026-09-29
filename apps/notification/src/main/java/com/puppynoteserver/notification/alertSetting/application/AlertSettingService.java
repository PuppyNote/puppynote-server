package com.puppynoteserver.notification.alertSetting.application;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.notification.alertSetting.application.port.in.AlertSettingFinder;
import com.puppynoteserver.notification.alertSetting.application.port.in.AlertSettingUpdater;
import com.puppynoteserver.notification.alertSetting.application.port.in.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.notification.alertSetting.application.port.in.response.AlertSettingResponse;
import com.puppynoteserver.notification.alertSetting.application.port.out.persistence.AlertSettingRepository;
import com.puppynoteserver.notification.alertSetting.domain.entity.AlertSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSettingService implements AlertSettingFinder, AlertSettingUpdater {

	private final SecurityService securityService;
	private final AlertSettingRepository alertSettingRepository;

	@Override
	public AlertSettingResponse getAlertSetting() {
		Long userId = securityService.getCurrentLoginUserInfo().getUserId();
		AlertSetting alertSetting = findByUserOrCreateDefault(userId);
		return AlertSettingResponse.createResponse(alertSetting);
	}

	@Override
	public AlertSetting findByUserOrCreateDefault(Long userId) {
		return alertSettingRepository.findByUserId(userId)
			.orElseGet(() -> alertSettingRepository.save(AlertSetting.createDefault(userId)));
	}

	@Override
	public Map<Long, AlertSetting> findAllByUserIds(List<Long> userIds) {
		return alertSettingRepository.findAllByUserIds(userIds).stream()
				.collect(Collectors.toMap(AlertSetting::getUserId, s -> s));
	}

	@Override
	public AlertSettingResponse updateAlertSetting(AlertSettingUpdateServiceRequest request) {
		Long userId = securityService.getCurrentLoginUserInfo().getUserId();
		AlertSetting alertSetting = findByUserOrCreateDefault(userId);

		alertSetting.updateAlertSettings(
				request.getAll(),
				request.getWalk(),
				request.getFriend()
		);

		return AlertSettingResponse.createResponse(alertSetting);
	}
}
