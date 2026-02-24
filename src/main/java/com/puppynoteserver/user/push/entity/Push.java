package com.puppynoteserver.user.push.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.user.users.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Push extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String deviceId;

	private String pushToken;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Builder
	private Push(String deviceId, User user, String pushToken) {
		this.deviceId = deviceId;
		this.user = user;
		this.pushToken = pushToken;
	}

	public static Push of(String deviceId, User user, String pushToken) {
		return Push.builder()
			.deviceId(deviceId)
			.user(user)
			.pushToken(pushToken)
			.build();
	}

	public void updatePushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public void updateUser(User user) {
		this.user = user;
		user.getPushes().add(this);
	}
}
