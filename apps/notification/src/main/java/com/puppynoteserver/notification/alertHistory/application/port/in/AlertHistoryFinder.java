package com.puppynoteserver.notification.alertHistory.application.port.in;

import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.notification.alertHistory.application.port.in.response.AlertHistoryResponse;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistory;

public interface AlertHistoryFinder {
    AlertHistory findByOne(Long id);

    PageCustom<AlertHistoryResponse> getAlertHistory(PageInfoServiceRequest request);

    boolean hasUncheckedAlerts();

    boolean hasFriendCode(Long userId, String friendCode);
}
