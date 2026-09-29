package com.puppynoteserver.notification.alertHistory.application.port.in.request;

import com.puppynoteserver.notification.alertHistory.domain.entity.AlertDestinationType;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistory;
import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistoryStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlertHistoryServiceRequest {

    private Long userId;
    private String alertDescription;
    private AlertDestinationType alertDestinationType;
    private String alertDestinationInfo;

    @Builder
    private AlertHistoryServiceRequest(Long userId, String alertDescription, AlertDestinationType alertDestinationType, String alertDestinationInfo) {
        this.userId = userId;
        this.alertDescription = alertDescription;
        this.alertDestinationType = alertDestinationType;
        this.alertDestinationInfo = alertDestinationInfo;
    }

    public AlertHistory toEntity() {
        return AlertHistory.builder()
                .userId(userId)
                .alertDescription(alertDescription)
                .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                .alertDestinationType(alertDestinationType)
                .alertDestinationInfo(alertDestinationInfo)
                .build();
    }
}
