package com.puppynoteserver.alertSetting.service.impl;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.repository.AlertSettingRepository;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSettingReadServiceImpl implements AlertSettingReadService {

	private final SecurityService securityService;
	private final UserReadService userReadService;
	private final AlertSettingRepository alertSettingRepository;

	@Override
	public AlertSettingResponse getAlertSetting() {
		User user = userReadService.findById(securityService.getCurrentLoginUserInfo().getUserId());
		AlertSetting alertSetting = findByUserOrCreateDefault(user);
		return AlertSettingResponse.createResponse(alertSetting);
	}

	@Override
	public AlertSetting findByUserOrCreateDefault(User user) {
		return alertSettingRepository.findByUser(user)
			.orElseGet(() -> alertSettingRepository.save(AlertSetting.createDefault(user)));
	}

	@Override
	public Map<Long, AlertSetting> findAllByUserIds(List<Long> userIds) {
		return alertSettingRepository.findAllByUserIds(userIds).stream()
				.collect(Collectors.toMap(s -> s.getUser().getId(), s -> s));
	}

}
