package com.puppynoteserver.foodChat.service.impl;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import com.puppynoteserver.foodChat.repository.FoodChatHistoryRepository;
import com.puppynoteserver.foodChat.service.FoodChatService;
import com.puppynoteserver.foodChat.service.FoodSearchService;
import com.puppynoteserver.foodChat.service.OllamaService;
import com.puppynoteserver.foodChat.service.request.FoodAiServiceRequest;
import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.foodChat.service.response.FoodResponse;
import com.puppynoteserver.global.exception.PuppyNoteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FoodChatServiceImpl implements FoodChatService {

    private final FoodChatHistoryRepository foodChatHistoryRepository;
    private final OllamaService ollamaService;
    private final FoodSearchService foodSearchService;

    @Override
    public FoodListResponse search(FoodChatServiceRequest request) {
        List<Long> ids = foodSearchService.search(request.question(), request.page(), request.size());
        if (ids.isEmpty()) {
            return FoodListResponse.of(List.of(), request.page(), 0L);
        }
        log.info("ES 캐시에서 음식 정보 반환: {}", request.question());
        List<FoodResponse> responses = foodChatHistoryRepository.findAllByIdIn(ids)
                .stream()
                .map(FoodResponse::of)
                .toList();
        return FoodListResponse.of(responses, request.page(), responses.size());
    }

    @Override
    public FoodResponse ask(FoodAiServiceRequest request) {
        String question = request.question();

        Optional<FoodChatHistory> existing = foodChatHistoryRepository.findByQuestion(question);
        if (existing.isPresent()) {
            log.info("DB에 동일 질문 존재, 기존 답변 반환: {}", question);
            return FoodResponse.of(existing.get());
        }

        OllamaService.OllamaResult result = ollamaService.ask(question);

        if (!result.isFood()) {
            throw new PuppyNoteException("음식에 관한 질문만 해주세요.");
        }

        FoodChatHistory saved = foodChatHistoryRepository.save(
                FoodChatHistory.of(question, result.answer(), result.safetyLevel())
        );

        return FoodResponse.of(saved);
    }
}
