package com.puppynoteserver.contracts.notification.api;

/**
 * 계약이 소유하는 알림 종류. notification 내부의 AlertDestinationType과 값 구성이 같아 보여도
 * 이 계약과 notification 내부 도메인 타입은 서로 독립이다 — notification 쪽 구현이 바뀌어도
 * 이 계약이 안 바뀌면 호출하는 서비스는 영향을 안 받는다.
 */
public enum NotificationType {
    DAILY_REPORT,
    FRIEND,
    FRIEND_CODE,
    WALK,
    PET_ITEM,
    FAMILY_INVITE
}
