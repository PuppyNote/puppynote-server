package com.puppynoteserver.notification.alertHistory.application.port.in;

import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryStatusResponse;

public interface AlertHistoryUpdater {
    AlertHistoryStatusResponse updateAlertHistoryStatus(Long id);
}
