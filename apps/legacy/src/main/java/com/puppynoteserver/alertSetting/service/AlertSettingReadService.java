package com.puppynoteserver.alertSetting.service;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.user.users.entity.User;

import java.util.List;
import java.util.Map;

public interface AlertSettingReadService {

	AlertSettingResponse getAlertSetting();

	AlertSetting findByUserOrCreateDefault(User user);

	Map<Long, AlertSetting> findAllByUserIds(List<Long> userIds);
}
