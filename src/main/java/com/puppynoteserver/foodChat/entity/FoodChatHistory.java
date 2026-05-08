package com.puppynoteserver.foodChat.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "food_chat_histories")
public class FoodChatHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SafetyLevel safetyLevel;

    public static FoodChatHistory of(String question, String answer, SafetyLevel safetyLevel) {
        FoodChatHistory history = new FoodChatHistory();
        history.question = question;
        history.answer = answer;
        history.safetyLevel = safetyLevel;
        return history;
    }

    public enum SafetyLevel {
        GOOD, NOTION, BAD
    }
}
