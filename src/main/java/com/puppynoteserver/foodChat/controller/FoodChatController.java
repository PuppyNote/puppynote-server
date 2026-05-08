package com.puppynoteserver.foodChat.controller;

import com.puppynoteserver.foodChat.controller.request.FoodChatRequest;
import com.puppynoteserver.foodChat.service.FoodChatService;
import com.puppynoteserver.foodChat.service.FoodSearchService;
import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.foodChat.service.response.FoodResponse;
import com.puppynoteserver.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodChatController {

    private final FoodChatService foodChatService;
    private final FoodSearchService foodSearchService;

    @GetMapping
    public ApiResponse<FoodListResponse> search(
            @RequestParam(required = false) String question,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(foodChatService.search(new FoodChatServiceRequest(question, page, size)));
    }

    @PostMapping("/ai")
    public ApiResponse<FoodResponse> ask(@RequestBody @Valid FoodChatRequest request) {
        return ApiResponse.ok(foodChatService.ask(request.toServiceRequest()));
    }

    @DeleteMapping("/documents/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable String id) {
        foodSearchService.delete(id);
        return ApiResponse.ok(null);
    }
}
