package com.puppynoteserver.foodChat.service.impl;

import com.puppynoteserver.foodChat.entity.FoodChatHistory;
import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import com.puppynoteserver.foodChat.repository.FoodChatHistoryRepository;
import com.puppynoteserver.foodChat.service.FoodChatService;
import com.puppynoteserver.foodChat.service.FoodSearchService;
import com.puppynoteserver.foodChat.service.OllamaService;
import com.puppynoteserver.foodChat.service.request.FoodChatServiceRequest;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.foodChat.service.response.FoodResponse;
import com.puppynoteserver.global.exception.PuppyNoteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        String question = request.question();
        int page = request.page();
        int size = request.size();

        // ES에서 ID 조회 → RDB에서 상세 데이터 조회
        List<Long> ids = foodSearchService.search(question, page, size);
        if (!ids.isEmpty()) {
            log.info("ES 캐시에서 음식 정보 반환: {}", question);
            List<FoodResponse> responses = foodChatHistoryRepository.findAllByIdIn(ids)
                    .stream()
                    .map(FoodResponse::of)
                    .toList();
            return FoodListResponse.of(responses, page, responses.size());
        }

        // AI 호출
        OllamaService.OllamaResult result = ollamaService.ask(question);

        if (!result.isFood()) {
            throw new PuppyNoteException("음식에 관한 질문만 해주세요.");
        }

        SafetyLevel safetyLevel = result.safetyLevel();
        FoodChatHistory saved = foodChatHistoryRepository.save(
                FoodChatHistory.of(question, result.answer(), safetyLevel)
        );

        return FoodListResponse.of(List.of(FoodResponse.of(saved)), 0, 1);
    }
}
