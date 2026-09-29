package com.puppynoteserver.notification.alertHistory.application.port.in.response;

import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistoryStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertHistoryStatusResponse {
    private final AlertHistoryStatus alertHistoryStatus;

    @Builder
    private AlertHistoryStatusResponse(AlertHistoryStatus alertHistoryStatus) {
        this.alertHistoryStatus = alertHistoryStatus;
    }

    public static AlertHistoryStatusResponse of(AlertHistoryStatus alertHistoryStatus) {
        return AlertHistoryStatusResponse.builder()
            .alertHistoryStatus(alertHistoryStatus)
            .build();
    }
}
