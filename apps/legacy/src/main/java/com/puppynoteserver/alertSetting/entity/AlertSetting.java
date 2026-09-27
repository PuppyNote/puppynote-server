package com.puppynoteserver.alertSetting.entity;

import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.user.users.entity.User;
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

	@OneToOne(fetch = FetchType.LAZY)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "all_alerts")
	private AlertType all;

	@Enumerated(EnumType.STRING)
	private AlertType walk;

	@Enumerated(EnumType.STRING)
	private AlertType friend;

	@Builder
	private AlertSetting(User user, AlertType all, AlertType walk, AlertType friend) {
		this.user = user;
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}

	public static AlertSetting createDefault(User user) {
		return AlertSetting.builder()
			.user(user)
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
