package com.puppynoteserver.global.exception;

import lombok.Getter;

/**
 * 다른 내부 서비스 호출(REST 등) 실패 시 던지는 예외.
 *
 * <p>
 * PuppyNoteException(400, 클라이언트 잘못)과 구분한다 — 이건 "요청 자체는 정상인데
 * 우리 쪽 인프라(다른 서비스 호출)가 실패했다"는 의미라 503으로 내려준다.
 */
@Getter
public class InfrastructureException extends RuntimeException {

	private final String message;

	public InfrastructureException(String message, Exception e) {
		super(message, e);
		this.message = message;
	}

	public InfrastructureException(String message) {
		this.message = message;
	}
}
