package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OllamaService {

    private static final String SYSTEM_PROMPT =
            "당신은 반려동물 영양 전문가입니다.\n" +
                    "사용자의 질문이 강아지 또는 반려동물에게 먹이는 음식에 관한 질문인지 판단하고 JSON으로 응답하세요.\n\n" +
                    "isFood: 음식 관련 질문이면 true, 아니면 false\n" +
                    "safetyLevel: GOOD(안전) / NOTION(주의) / BAD(위험) / null (음식 아닐 때)\n" +
                    "answer: 음식 관련 질문이면 영양 정보, 주의사항, 적절한 섭취량을 한국어로 상세히 작성, 아니면 null";

    private final ChatClient chatClient;

    public OllamaService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public OllamaResult ask(String question) {
        try {
            FoodAnalysis analysis = chatClient.prompt()
                    .user(question)
                    .call()
                    .entity(FoodAnalysis.class);

            if (analysis == null || !analysis.isFood()) {
                return OllamaResult.notFood();
            }

            SafetyLevel safetyLevel = analysis.safetyLevel() != null
                    ? SafetyLevel.valueOf(analysis.safetyLevel())
                    : null;

            return OllamaResult.food(analysis.answer(), safetyLevel);
        } catch (Exception e) {
            log.error("Ollama 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.");
        }
    }

    record FoodAnalysis(boolean isFood, String safetyLevel, String answer) {}

    public record OllamaResult(boolean isFood, String answer, SafetyLevel safetyLevel) {
        public static OllamaResult food(String answer, SafetyLevel safetyLevel) {
            return new OllamaResult(true, answer, safetyLevel);
        }

        public static OllamaResult notFood() {
            return new OllamaResult(false, null, null);
        }
    }
}
