package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OllamaService {

    private static final String SYSTEM_PROMPT =
            "당신은 반려동물 영양 전문가입니다.\n" +
                    "사용자의 질문이 강아지 또는 반려동물에게 먹이는 음식에 관한 질문인지 판단하세요.\n\n" +
                    "음식 관련 질문이 아니라면: 정확히 \"<not_food/>\" 라고만 응답하세요. 다른 내용은 절대 추가하지 마세요.\n\n" +
                    "음식 관련 질문이라면: 반드시 아래 형식을 그대로 사용하세요.\n\n" +
                    "<safety>GOOD</safety>\n" +
                    "<description>\n" +
                    "설명 내용\n" +
                    "</description>\n\n" +
                    "<safety> 태그 안에는 아래 셋 중 하나만 작성하세요.\n" +
                    "- 강아지에게 안전하다면: GOOD\n" +
                    "- 주의가 필요하다면: NOTION\n" +
                    "- 절대 먹이면 안 된다면: BAD\n\n" +
                    "<description> 태그 안에 해당 음식이 강아지에게 안전한지, 영양 정보, 주의사항, 적절한 섭취량 등에 대해 " +
                    "친절하고 자세하게 한국어로 답변해주세요.";

    private static final String NOT_FOOD_MARKER = "not_food";
    private static final Pattern SAFETY_PATTERN = Pattern.compile("<safety>\\s*(GOOD|NOTION|BAD)\\s*</safety>", Pattern.CASE_INSENSITIVE);
    private static final String DESC_OPEN_TAG = "<description>";

    private final ChatClient chatClient;

    public OllamaService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public OllamaResult ask(String question) {
        try {
            String content = chatClient.prompt()
                    .user(question)
                    .call()
                    .content();

            if (content == null || content.contains(NOT_FOOD_MARKER)) {
                return OllamaResult.notFood();
            }

            Matcher safetyMatcher = SAFETY_PATTERN.matcher(content);
            if (safetyMatcher.find()) {
                SafetyLevel safetyLevel = SafetyLevel.valueOf(safetyMatcher.group(1).trim().toUpperCase());

                int descStart = content.indexOf(DESC_OPEN_TAG);
                String answer;
                if (descStart != -1) {
                    String afterTag = content.substring(descStart + DESC_OPEN_TAG.length());
                    int descEnd = afterTag.lastIndexOf("</description>");
                    answer = (descEnd != -1 ? afterTag.substring(0, descEnd) : afterTag).trim();
                } else {
                    answer = content.trim();
                }
                return OllamaResult.food(answer, safetyLevel);
            }

            // safety 태그 파싱 실패 시 safetyLevel 없이 반환
            log.warn("AI 응답에서 safety 태그를 파싱하지 못했습니다. 응답: {}", content);
            return OllamaResult.food(content.trim(), null);
        } catch (Exception e) {
            log.error("Ollama 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.");
        }
    }

    public record OllamaResult(boolean isFood, String answer, SafetyLevel safetyLevel) {
        public static OllamaResult food(String answer, SafetyLevel safetyLevel) {
            return new OllamaResult(true, answer, safetyLevel);
        }

        public static OllamaResult notFood() {
            return new OllamaResult(false, null, null);
        }
    }
}
