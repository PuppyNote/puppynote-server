package com.puppynoteserver.alertHistory.service.alertHistory;

import com.puppynoteserver.alertHistory.entity.AlertHistory;
import com.puppynoteserver.alertHistory.entity.AlertHistoryStatus;
import com.puppynoteserver.alertHistory.repository.AlertHistoryRepository;
import com.puppynoteserver.alertHistory.service.alertHistory.request.AlertHistoryServiceRequest;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertHistoryService {

    private final AlertHistoryRepository alertHistoryRepository;
    private final AlertHistoryReadService alertHistoryReadService;

    public AlertHistory createAlertHistory(AlertHistoryServiceRequest alertHistoryServiceRequest) {
        return alertHistoryRepository.save(alertHistoryServiceRequest.toEntity());
    }

    public AlertHistoryStatusResponse updateAlertHistoryStatus(Long id) {
        AlertHistory alertHistory = alertHistoryReadService.findByOne(id);
        alertHistory.updateAlertHistoryStatus(AlertHistoryStatus.CHECKED);

        return AlertHistoryStatusResponse.of(alertHistory.getAlertHistoryStatus());
    }
}
