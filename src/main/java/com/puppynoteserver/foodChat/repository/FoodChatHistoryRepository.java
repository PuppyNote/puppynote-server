package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;

import java.util.List;

public interface FoodChatHistoryRepository {
    FoodChatHistory save(FoodChatHistory foodChatHistory);
    List<FoodChatHistory> findAllByIdIn(List<Long> ids);
}
