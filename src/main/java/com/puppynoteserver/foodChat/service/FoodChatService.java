package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.service.request.FoodAiServiceRequest;
import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.foodChat.service.response.FoodResponse;

public interface FoodChatService {
    FoodListResponse search(FoodChatServiceRequest request);
    FoodResponse ask(FoodAiServiceRequest request);
}
