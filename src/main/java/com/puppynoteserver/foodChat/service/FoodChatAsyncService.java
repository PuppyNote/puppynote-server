package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import com.puppynoteserver.foodChat.repository.FoodChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodChatAsyncService {

    private final OllamaService ollamaService;
    private final FoodChatHistoryRepository foodChatHistoryRepository;

    @Async
    @Transactional
    public void askWithOllamaAndSave(String question) {
        try {
            log.info("Ollama 비동기 처리 시작: {}", question);
            AiResult result = ollamaService.ask(question);
            if (result.isFood()) {
                foodChatHistoryRepository.findByQuestion(question).ifPresentOrElse(
                        existing -> log.info("이미 저장된 응답 존재, 건너뜀: {}", question),
                        () -> {
                            foodChatHistoryRepository.save(
                                    FoodChatHistory.of(question, result.answer(), result.safetyLevel())
                            );
                            log.info("Ollama 응답 저장 완료: {}", question);
                        }
                );
            }
        } catch (Exception e) {
            log.error("Ollama 비동기 처리 실패 - 질문: {}, 오류: {}", question, e.getMessage());
        }
    }
}
