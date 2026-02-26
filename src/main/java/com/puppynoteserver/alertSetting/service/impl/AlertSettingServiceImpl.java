package com.puppynoteserver.alertSetting.service.impl;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.alertSetting.service.AlertSettingService;
import com.puppynoteserver.alertSetting.service.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSettingServiceImpl implements AlertSettingService {

	private final SecurityService securityService;
	private final UserReadService userReadService;
	private final AlertSettingReadService alertSettingReadService;

	@Override
	public AlertSettingResponse updateAlertSetting(AlertSettingUpdateServiceRequest request) {
		User user = userReadService.findById(securityService.getCurrentLoginUserInfo().getUserId());
		AlertSetting alertSetting = alertSettingReadService.findByUserOrCreateDefault(user);

		alertSetting.updateAlertSettings(
			request.getAll(),
			request.getWalk(),
			request.getFriend()
		);

		return AlertSettingResponse.createResponse(alertSetting);
	}
}
