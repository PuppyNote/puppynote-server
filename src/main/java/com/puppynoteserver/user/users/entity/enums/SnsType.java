package com.puppynoteserver.user.users.entity.enums;

import java.util.Arrays;
import java.util.Optional;

import com.puppynoteserver.global.exception.PuppyNoteException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SnsType {
	NORMAL("일반"),
	KAKAO("카카오"),
	GOOGLE("구글"),
	NAVER("네이버");

	private final String text;

	public void checkSnsType() {
		Optional<SnsType> snsType = Arrays.stream(values())
			.filter(type -> type.getText().equals(text))
			.findFirst();
		snsType.ifPresent(type -> {
			throw new PuppyNoteException(type.name());
		});
	}
}
