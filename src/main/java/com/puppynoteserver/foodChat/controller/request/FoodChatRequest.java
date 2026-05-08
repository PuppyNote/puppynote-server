package com.puppynoteserver.foodChat.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FoodChatRequest {

    @NotBlank(message = "질문을 입력해주세요.")
    private final String question;

    public FoodChatRequest(String question) {
        this.question = question;
    }
}
