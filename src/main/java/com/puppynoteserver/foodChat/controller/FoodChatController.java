package com.puppynoteserver.foodChat.controller;

import com.puppynoteserver.foodChat.service.FoodChatService;
import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.global.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodChatController {

    private final FoodChatService foodChatService;

    @GetMapping
    public ApiResponse<FoodListResponse> search(
            @RequestParam @NotBlank(message = "질문을 입력해주세요.") String question,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(foodChatService.search(new FoodChatServiceRequest(question, page, size)));
    }
}
