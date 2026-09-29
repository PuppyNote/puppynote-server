package com.puppynoteserver.notification.alertSetting.adapter.out.persistence;

import com.puppynoteserver.notification.alertSetting.domain.entity.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlertSettingJpaRepository extends JpaRepository<AlertSetting, Long> {
	Optional<AlertSetting> findByUserId(Long userId);

	@Query("SELECT a FROM AlertSetting a WHERE a.userId IN :userIds")
	List<AlertSetting> findAllByUserIdIn(@Param("userIds") List<Long> userIds);
}
