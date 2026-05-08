package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;

public interface FoodChatService {
    FoodListResponse search(FoodChatServiceRequest request);
}
