package com.puppynoteserver.user.user.adapter.out.client;

import com.puppynoteserver.user.user.application.port.out.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * [ADAPTER · OUT] JavaMailSender 기반 SMTP 발송.
 */
@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements EmailSender {

    private static final String SENDER_EMAIL = "puppynote0330@gmail.com";
    private static final String SENDER_NAME = "PuppyNote";

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }
}
