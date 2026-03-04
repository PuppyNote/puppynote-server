package com.puppynoteserver.firebase.enums;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PushMessage {

	WALK("산책 알림"),
	PET_ITEM("용품 구매 알림"),
	FRIEND("친구 추가 알림"),
	FRIEND_CODE("친구 요청 알림"),
	DAILY_REPORT("일일 리포트"),
	FAMILY_INVITE("가족 초대 알림"),
	DEFAULT("퍼피노트 알림");

	private final String text;

	public static PushMessage from(AlertDestinationType type) {
		return switch (type) {
			case WALK -> WALK;
			case PET_ITEM -> PET_ITEM;
			case FRIEND -> FRIEND;
			case FRIEND_CODE -> FRIEND_CODE;
			case DAILY_REPORT -> DAILY_REPORT;
			case FAMILY_INVITE -> FAMILY_INVITE;
		};
	}
}
