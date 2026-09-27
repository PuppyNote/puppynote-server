package com.puppynoteserver.foodChat.service.response;

import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import lombok.Getter;

@Getter
public class FoodChatResponse {

    private final String question;
    private final String answer;
    private final String source;
    private final SafetyLevel safetyLevel;

    private FoodChatResponse(String question, String answer, String source, SafetyLevel safetyLevel) {
        this.question = question;
        this.answer = answer;
        this.source = source;
        this.safetyLevel = safetyLevel;
    }

    public static FoodChatResponse of(String question, String answer, String source, SafetyLevel safetyLevel) {
        return new FoodChatResponse(question, answer, source, safetyLevel);
    }
}
