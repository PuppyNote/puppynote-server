package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FoodChatHistoryRepositoryImpl implements FoodChatHistoryRepository {

    private final FoodChatHistoryJpaRepository jpaRepository;

    @Override
    public FoodChatHistory save(FoodChatHistory foodChatHistory) {
        return jpaRepository.save(foodChatHistory);
    }

    @Override
    public List<FoodChatHistory> findAllByIdIn(List<Long> ids) {
        return new ArrayList<>(jpaRepository.findAllById(ids));
    }
}
