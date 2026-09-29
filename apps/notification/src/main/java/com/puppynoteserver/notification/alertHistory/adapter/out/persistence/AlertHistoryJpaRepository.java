package com.puppynoteserver.notification.alertHistory.adapter.out.persistence;

import com.puppynoteserver.notification.alertHistory.domain.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertHistoryJpaRepository extends JpaRepository<AlertHistory, Long> {

	void deleteByUserId(Long userId);

	List<AlertHistory> findByUserId(Long userId);
}
