package com.puppynoteserver.alertSetting.repository.impl;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.repository.AlertSettingJpaRepository;
import com.puppynoteserver.alertSetting.repository.AlertSettingRepository;
import com.puppynoteserver.user.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlertSettingRepositoryImpl implements AlertSettingRepository {

	private final AlertSettingJpaRepository alertSettingJpaRepository;

	@Override
	public AlertSetting save(AlertSetting alertSetting) {
		return alertSettingJpaRepository.save(alertSetting);
	}

	@Override
	public Optional<AlertSetting> findByUser(User user) {
		return alertSettingJpaRepository.findByUser(user);
	}

	@Override
	public void deleteAllInBatch() {
		alertSettingJpaRepository.deleteAllInBatch();
	}
}
