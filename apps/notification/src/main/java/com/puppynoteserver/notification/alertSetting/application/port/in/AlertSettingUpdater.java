package com.puppynoteserver.notification.alertSetting.application.port.in;

import com.puppynoteserver.notification.alertSetting.application.port.in.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.notification.alertSetting.application.port.in.response.AlertSettingResponse;

public interface AlertSettingUpdater {

    AlertSettingResponse updateAlertSetting(AlertSettingUpdateServiceRequest request);

}
