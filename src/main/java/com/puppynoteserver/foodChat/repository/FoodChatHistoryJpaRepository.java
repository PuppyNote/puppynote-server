package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodChatHistoryJpaRepository extends JpaRepository<FoodChatHistory, Long> {
}
