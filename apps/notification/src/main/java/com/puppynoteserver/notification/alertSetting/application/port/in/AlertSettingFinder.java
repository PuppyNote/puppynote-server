package com.puppynoteserver.notification.alertSetting.application.port.in;

import com.puppynoteserver.notification.alertSetting.domain.entity.AlertSetting;
import com.puppynoteserver.notification.alertSetting.application.port.in.response.AlertSettingResponse;

import java.util.List;
import java.util.Map;

public interface AlertSettingFinder {

	AlertSettingResponse getAlertSetting();

	AlertSetting findByUserOrCreateDefault(Long userId);

	Map<Long, AlertSetting> findAllByUserIds(List<Long> userIds);
}
