package com.puppynoteserver.user.push.entity.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.puppynoteserver.global.exception.PuppyNoteException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 푸시 디바이스 토큰이 발급된 플랫폼 구분값.
 * 네이티브 앱뿐 아니라 웹뷰/모바일 웹에서도 동일한 등록 API를 재사용하기 위해 사용한다.
 */
@Getter
@RequiredArgsConstructor
public enum DevicePlatform {

	IOS("iOS"),
	ANDROID("안드로이드"),
	WEB("웹");

	private final String description;

	/**
	 * 클라이언트가 대소문자를 섞어 보내도(ios, Web 등) 허용한다.
	 */
	@JsonCreator
	public static DevicePlatform from(String value) {
		return Arrays.stream(values())
			.filter(platform -> platform.name().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new PuppyNoteException("지원하지 않는 플랫폼입니다. (IOS, ANDROID, WEB)"));
	}
}
