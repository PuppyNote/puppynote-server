package com.puppynoteserver.alertSetting.service;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.user.users.entity.User;

public interface AlertSettingReadService {

	AlertSettingResponse getAlertSetting();

	AlertSetting findByUserOrCreateDefault(User user);
}
