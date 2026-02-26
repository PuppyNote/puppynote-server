package com.puppynoteserver.alertHistory.repository;

import com.puppynoteserver.alertHistory.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertHistoryJpaRepository extends JpaRepository<AlertHistory, Long> {

	void deleteByUserId(Long userId);

	List<AlertHistory> findByUserId(Long userId);
}
