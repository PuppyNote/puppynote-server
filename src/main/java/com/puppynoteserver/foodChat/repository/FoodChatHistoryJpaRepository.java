package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodChatHistoryJpaRepository extends JpaRepository<FoodChatHistory, Long> {
    Optional<FoodChatHistory> findByQuestion(String question);
    List<FoodChatHistory> findByQuestionContaining(String question, Pageable pageable);
}
