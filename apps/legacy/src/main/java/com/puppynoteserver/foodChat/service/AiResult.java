package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;

public record AiResult(boolean isFood, String answer, SafetyLevel safetyLevel) {

    public static AiResult food(String answer, SafetyLevel safetyLevel) {
        return new AiResult(true, answer, safetyLevel);
    }

    public static AiResult notFood() {
        return new AiResult(false, null, null);
    }
}
