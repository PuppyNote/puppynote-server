package com.puppynoteserver.global.email;

public interface EmailService {
    String sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
}
