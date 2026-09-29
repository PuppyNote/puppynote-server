package com.puppynoteserver.user.user.application.port.out;

/**
 * [APPLICATION · OUT PORT] 이메일 발송기.
 *
 * <p>
 * 포트로 뽑아 두는 이유는 제공자가 계약이 아니기 때문이다 — SMTP(JavaMailSender)에서
 * SES 같은 다른 발송 수단으로 바꿔도 {@code EmailService}는 변경되지 않는다.
 */
public interface EmailSender {

    void send(String to, String subject, String html);
}
