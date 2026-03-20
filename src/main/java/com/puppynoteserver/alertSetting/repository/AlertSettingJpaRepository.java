package com.puppynoteserver.alertSetting.repository;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.user.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlertSettingJpaRepository extends JpaRepository<AlertSetting, Long> {
	Optional<AlertSetting> findByUser(User user);

	@Query("SELECT a FROM AlertSetting a WHERE a.user.id IN :userIds")
	List<AlertSetting> findAllByUserIdIn(@Param("userIds") List<Long> userIds);
}
