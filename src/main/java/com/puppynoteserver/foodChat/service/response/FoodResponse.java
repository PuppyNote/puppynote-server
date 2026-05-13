package com.puppynoteserver.foodChat.service.response;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import lombok.Getter;

@Getter
public class FoodResponse {

    private final Long id;
    private final String question;
    private final String answer;
    private final SafetyLevel safetyLevel;

    private FoodResponse(Long id, String question, String answer, SafetyLevel safetyLevel) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.safetyLevel = safetyLevel;
    }

    public static FoodResponse of(FoodChatHistory history) {
        return new FoodResponse(history.getId(), history.getQuestion(), history.getAnswer(), history.getSafetyLevel());
    }
}
