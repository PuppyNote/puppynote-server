package com.puppynoteserver.alertSetting.repository;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.user.users.entity.User;

import java.util.List;
import java.util.Optional;

public interface AlertSettingRepository {
	AlertSetting save(AlertSetting alertSetting);

	Optional<AlertSetting> findByUser(User user);

	List<AlertSetting> findAllByUserIds(List<Long> userIds);

	void deleteAllInBatch();
}
