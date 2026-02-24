package com.puppynoteserver.global.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationStore verificationStore;

    private static final String SENDER_EMAIL = "puppynote0330@gmail.com";
    private static final String SENDER_NAME = "PuppyNote";

    @Override
    public String sendVerificationCode(String email) {
        String code = generateCode();
        verificationStore.save(email, code);
        sendEmail(email, code);
        return code;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        boolean result = verificationStore.verify(email, code);
        if (result) {
            verificationStore.remove(email);
        }
        return result;
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(to);
            helper.setSubject("[PuppyNote] 이메일 인증번호를 확인해주세요");
            helper.setText(buildHtml(code), true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    private String buildHtml(String code) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:'Apple SD Gothic Neo',Arial,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;">
                    <tr>
                        <td align="center" style="padding:48px 16px;">
                            <table width="560" cellpadding="0" cellspacing="0" border="0"
                                   style="max-width:560px;width:100%%;background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(0,0,0,0.08);">

                                <!-- Header -->
                                <tr>
                                    <td style="background:linear-gradient(135deg,#eebd2b 0%%,#f5d571 100%%);padding:44px 48px 36px;text-align:center;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                            <tr>
                                                <td align="center">
                                                    <div style="display:inline-block;background:rgba(255,255,255,0.25);border-radius:50%%;width:72px;height:72px;line-height:72px;font-size:36px;margin-bottom:16px;">🐾</div>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td align="center">
                                                    <p style="margin:0;font-size:32px;font-weight:800;color:#ffffff;letter-spacing:-1px;text-shadow:0 2px 4px rgba(0,0,0,0.1);">PuppyNote</p>
                                                    <p style="margin:6px 0 0;font-size:14px;color:rgba(255,255,255,0.9);font-weight:500;letter-spacing:0.5px;">이메일 인증</p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Body -->
                                <tr>
                                    <td style="padding:48px 48px 40px;">
                                        <p style="margin:0 0 6px;font-size:24px;font-weight:700;color:#1a1a1a;letter-spacing:-0.5px;">안녕하세요! 👋</p>
                                        <p style="margin:0 0 36px;font-size:15px;color:#666;line-height:1.8;">
                                            PuppyNote를 이용해 주셔서 감사합니다.<br>
                                            아래 인증번호를 입력하여 이메일 인증을 완료해 주세요.
                                        </p>

                                        <!-- Code Box -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom:36px;">
                                            <tr>
                                                <td align="center" style="background:#fffbec;border:2px solid #eebd2b;border-radius:16px;padding:36px 24px;">
                                                    <p style="margin:0 0 12px;font-size:11px;font-weight:700;color:#eebd2b;letter-spacing:3px;text-transform:uppercase;">인증번호</p>
                                                    <p style="margin:0;font-size:52px;font-weight:800;color:#1a1a1a;letter-spacing:16px;font-variant-numeric:tabular-nums;">%s</p>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Notice -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                            <tr>
                                                <td style="background:#f9f9f9;border-radius:10px;padding:20px 24px;">
                                                    <p style="margin:0 0 6px;font-size:13px;color:#555;line-height:1.7;">
                                                        ⏰ &nbsp;인증번호는 <strong style="color:#eebd2b;">5분</strong> 후 만료됩니다.
                                                    </p>
                                                    <p style="margin:0;font-size:13px;color:#999;line-height:1.7;">
                                                        🔒 &nbsp;본인이 요청하지 않은 경우, 이 메일을 무시하셔도 됩니다.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Divider -->
                                <tr>
                                    <td style="padding:0 48px;">
                                        <hr style="border:none;border-top:1px solid #eeeeee;margin:0;">
                                    </td>
                                </tr>

                                <!-- Footer -->
                                <tr>
                                    <td style="padding:28px 48px;text-align:center;">
                                        <p style="margin:0 0 8px;font-size:20px;">🐶</p>
                                        <p style="margin:0 0 4px;font-size:12px;font-weight:600;color:#eebd2b;">PuppyNote</p>
                                        <p style="margin:0;font-size:11px;color:#bbb;line-height:1.7;">
                                            반려동물과의 소중한 일상을 기록하세요<br>
                                            © 2025 PuppyNote. All rights reserved.
                                        </p>
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """.formatted(code);
    }
}
