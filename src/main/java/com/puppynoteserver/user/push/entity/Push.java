package com.puppynoteserver.user.push.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.user.push.entity.enums.DevicePlatform;
import com.puppynoteserver.user.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Column(unique = true)
	private String deviceId;

	private String pushToken;

	/**
	 * 기존 로그인 경로로 등록된 디바이스에는 값이 없을 수 있어 nullable로 둔다.
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private DevicePlatform platform;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Builder
	private Push(String deviceId, User user, String pushToken, DevicePlatform platform) {
		this.deviceId = deviceId;
		this.user = user;
		this.pushToken = pushToken;
		this.platform = platform;
	}

	public static Push of(String deviceId, User user, String pushToken) {
		return of(deviceId, user, pushToken, null);
	}

	public static Push of(String deviceId, User user, String pushToken, DevicePlatform platform) {
		return Push.builder()
			.deviceId(deviceId)
			.user(user)
			.pushToken(pushToken)
			.platform(platform)
			.build();
	}

	public void updatePushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	/**
	 * 디바이스 토큰과 플랫폼을 함께 갱신한다.
	 * platform이 null이면 기존 값을 유지한다. (플랫폼을 보내지 않는 기존 로그인 경로 호환)
	 */
	public void updateDeviceToken(String pushToken, DevicePlatform platform) {
		this.pushToken = pushToken;
		if (platform != null) {
			this.platform = platform;
		}
	}

	public void updateUser(User user) {
		this.user = user;
		user.getPushes().add(this);
	}
}
