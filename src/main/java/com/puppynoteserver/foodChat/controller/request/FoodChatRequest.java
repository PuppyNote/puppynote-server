package com.puppynoteserver.foodChat.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.puppynoteserver.foodChat.service.request.FoodAiServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FoodChatRequest {

    @NotBlank(message = "질문을 입력해주세요.")
    private final String question;

    @JsonCreator
    public FoodChatRequest(@JsonProperty("question") String question) {
        this.question = question;
    }

    public FoodAiServiceRequest toServiceRequest() {
        return new FoodAiServiceRequest(question);
    }
}
