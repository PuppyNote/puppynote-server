package com.puppynoteserver.foodChat.service;

public class GeminiQuotaExceededException extends RuntimeException {

    public GeminiQuotaExceededException() {
        super("Gemini API 할당량 초과");
    }
}
