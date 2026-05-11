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
                    "사용자의 입력이 강아지에게 먹일 수 있는 음식(재료, 식품)인지 판단하고 JSON으로 응답하세요.\n\n" +
                    "[판단 원칙]\n" +
                    "- 입력된 단어가 음식으로 해석될 가능성이 있으면 반드시 음식으로 분류하세요.\n" +
                    "- 한국어는 동일 단어가 여러 의미를 가질 수 있습니다. 이 서비스는 음식 전용이므로 음식 의미를 최우선으로 해석하세요.\n" +
                    "- 예: '배'는 과일(梨), '밤'은 밤(栗), '닭'은 닭고기, '감'은 감(柿)으로 해석하세요.\n" +
                    "- 음식이 아닌 것: 산책, 목욕, 훈련, 장난감처럼 명백히 음식과 무관한 단어만 false로 분류하세요.\n\n" +
                    "[응답 형식]\n" +
                    "isFood: 음식이면 true, 명백히 음식이 아니면 false\n" +
                    "safetyLevel: GOOD(안전) / NOTION(주의) / BAD(위험) / null(음식 아닐 때)\n" +
                    "answer: 음식이면 아래 구조로 마크다운 형식(**, 번호 목록)으로 작성. 음식이 아니면 null.\n" +
                    "  - 첫 줄: '{음식명}은(는) 강아지에게 {안전/주의/위험}한 음식입니다.' 로 시작하는 한 줄 요약\n" +
                    "  - 1. **영양 정보**: 주요 영양소와 건강 효능\n" +
                    "  - 2. **섭취 방법**: 준비 방법, 적정 양, 제공 방식\n" +
                    "  - 3. **주의사항**: 부작용, 알레르기, 특정 건강 상태 시 주의점 (소항목은 - **소제목**: 내용 형식)\n\n" +
                    "[예시]\n" +
                    "입력: '배' → isFood: true (과일 배, 영어명 pear)\n" +
                    "입력: '밤' → isFood: true (밤 열매, 영어명 chestnut)\n" +
                    "입력: '감' → isFood: true (감 과일, 영어명 persimmon)\n" +
                    "입력: '닭' → isFood: true (닭고기)\n" +
                    "입력: '산책' → isFood: false";

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
