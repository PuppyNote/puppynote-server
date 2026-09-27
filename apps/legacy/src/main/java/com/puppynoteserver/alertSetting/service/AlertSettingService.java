package com.puppynoteserver.alertSetting.service;

import com.puppynoteserver.alertSetting.service.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;

public interface AlertSettingService {

	AlertSettingResponse updateAlertSetting(AlertSettingUpdateServiceRequest request);
}
