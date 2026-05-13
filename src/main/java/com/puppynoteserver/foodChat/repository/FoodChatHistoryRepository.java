package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;

import java.util.List;
import java.util.Optional;

public interface FoodChatHistoryRepository {
    FoodChatHistory save(FoodChatHistory foodChatHistory);
    List<FoodChatHistory> findAllByIdIn(List<Long> ids);
    Optional<FoodChatHistory> findByQuestion(String question);
    List<FoodChatHistory> findAllByQuestionContaining(String question, int page, int size);
    List<FoodChatHistory> findAllOrderByCreatedDateDesc(int page, int size);
    long count();
}
