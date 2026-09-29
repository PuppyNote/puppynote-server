package com.puppynoteserver.notification.alertSetting.application.port.out.persistence;

import com.puppynoteserver.notification.alertSetting.domain.entity.AlertSetting;

import java.util.List;
import java.util.Optional;

public interface AlertSettingRepository {
	AlertSetting save(AlertSetting alertSetting);

	Optional<AlertSetting> findByUserId(Long userId);

	List<AlertSetting> findAllByUserIds(List<Long> userIds);

	void deleteAllInBatch();
}
