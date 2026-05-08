package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Override
    public List<FoodChatHistory> findAllOrderByCreatedDateDesc(int page, int size) {
        return jpaRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))
        ).getContent();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
