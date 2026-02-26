package com.puppynoteserver.alertSetting.repository;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.user.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertSettingJpaRepository extends JpaRepository<AlertSetting, Long> {
	Optional<AlertSetting> findByUser(User user);
}
