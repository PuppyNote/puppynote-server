package com.puppynoteserver.notification.alertSetting.domain.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.notification.alertSetting.domain.enums.AlertType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AlertSetting extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "all_alerts")
	private AlertType all;

	@Enumerated(EnumType.STRING)
	private AlertType walk;

	@Enumerated(EnumType.STRING)
	private AlertType friend;

	@Builder
	private AlertSetting(Long userId, AlertType all, AlertType walk, AlertType friend) {
		this.userId = userId;
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}

	public static AlertSetting createDefault(Long userId) {
		return AlertSetting.builder()
			.userId(userId)
			.all(AlertType.ON)
			.walk(AlertType.ON)
			.friend(AlertType.ON)
			.build();
	}

	public void updateAlertSettings(AlertType all, AlertType walk, AlertType friend) {
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}
}
