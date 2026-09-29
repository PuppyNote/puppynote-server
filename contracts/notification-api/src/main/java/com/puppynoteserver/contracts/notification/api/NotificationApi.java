package com.puppynoteserver.contracts.notification.api;

/**
 * notification 서비스 내부 API 경로. 발행자(apps/notification)와 소비자(다른 서비스들)가 같은
 * 상수를 쓰게 계약 모듈이 소유한다.
 */
public final class NotificationApi {

    /** 유저 한 명에게 푸시 발송을 요청한다. 응답은 즉시 오고, 실제 발송은 notification이 비동기로 처리한다. */
    public static final String PUSH = "/internal/v1/notifications/push";

    private NotificationApi() {
    }
}
