package com.puppynoteserver.notification.alertSetting.adapter.out.persistence;

import com.puppynoteserver.notification.alertSetting.application.port.out.persistence.AlertSettingRepository;
import com.puppynoteserver.notification.alertSetting.domain.entity.AlertSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
	public Optional<AlertSetting> findByUserId(Long userId) {
		return alertSettingJpaRepository.findByUserId(userId);
	}

	@Override
	public List<AlertSetting> findAllByUserIds(List<Long> userIds) {
		if (userIds.isEmpty()) return List.of();
		return alertSettingJpaRepository.findAllByUserIdIn(userIds);
	}

	@Override
	public void deleteAllInBatch() {
		alertSettingJpaRepository.deleteAllInBatch();
	}
}
