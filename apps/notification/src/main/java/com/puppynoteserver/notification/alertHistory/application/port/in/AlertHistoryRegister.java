package com.puppynoteserver.notification.alertHistory.application.port.in;

import com.puppynoteserver.notification.alertHistory.application.port.in.request.AlertHistoryServiceRequest;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistory;

public interface AlertHistoryRegister {
    AlertHistory createAlertHistory(AlertHistoryServiceRequest alertHistoryServiceRequest);
}
