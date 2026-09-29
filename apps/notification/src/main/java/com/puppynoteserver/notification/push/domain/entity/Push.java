package com.puppynoteserver.notification.push.domain.entity;

import com.puppynoteserver.global.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 서비스가 직접 관리하는 디바이스 푸시 토큰.
 *
 * <p>
 * user 서비스가 별도로 분리될 것을 대비해 {@code User} 엔티티를 참조(@ManyToOne)하지 않고 {@code userId}만 갖는다 —
 * chatplanet-server의 apps/push가 user 엔티티를 (appId, userId)만 있는 로컬 사본으로 두는 것과 같은 이유다.
 */
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

	private Long userId;

	@Builder
	private Push(String deviceId, Long userId, String pushToken) {
		this.deviceId = deviceId;
		this.userId = userId;
		this.pushToken = pushToken;
	}

	public static Push of(String deviceId, Long userId, String pushToken) {
		return Push.builder()
			.deviceId(deviceId)
			.userId(userId)
			.pushToken(pushToken)
			.build();
	}

	public void updatePushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public void updateUser(Long userId) {
		this.userId = userId;
	}
}
