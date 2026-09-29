package com.puppynoteserver.notification.shared.application.port.out;

import java.util.List;

/**
 * [APPLICATION · OUT PORT] 외부 푸시 발송기.
 *
 * <p>
 * 포트로 뽑아 두는 이유는 제공자가 계약이 아니기 때문이다 — Expo에서 FCM 직접 연동 등으로 바꿔도
 * {@code PushDispatchService}는 변경되지 않는다.
 */
public interface PushSender {

    void sendBatch(List<PushSendCommand> commands);
}
