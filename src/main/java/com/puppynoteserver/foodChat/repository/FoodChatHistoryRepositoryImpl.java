package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<FoodChatHistory> findByQuestion(String question) {
        return jpaRepository.findByQuestion(question);
    }
}
