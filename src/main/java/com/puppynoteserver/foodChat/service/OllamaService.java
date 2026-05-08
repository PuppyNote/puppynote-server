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
                    "음식 관련 질문이 아니라면: 정확히 \"[NOT_FOOD]\" 라고만 응답하세요. 다른 내용은 절대 추가하지 마세요.\n\n" +
                    "음식 관련 질문이라면: 반드시 답변 첫 줄에 아래 중 하나를 단독으로 작성하세요.\n" +
                    "- 강아지에게 안전하다면: [GOOD]\n" +
                    "- 주의가 필요하다면: [NOTION]\n" +
                    "- 절대 먹이면 안 된다면: [BAD]\n\n" +
                    "두 번째 줄부터 해당 음식이 강아지에게 안전한지, 영양 정보, 주의사항, 적절한 섭취량 등에 대해 " +
                    "친절하고 자세하게 한국어로 답변해주세요.";

    private static final String NOT_FOOD_MARKER = "NOT_FOOD";
    private static final Pattern SAFETY_PATTERN = Pattern.compile("\\[(GOOD|NOTION|BAD)\\]");

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

            Matcher matcher = SAFETY_PATTERN.matcher(content);
            if (matcher.find()) {
                SafetyLevel safetyLevel = SafetyLevel.valueOf(matcher.group(1));
                String answer = content.replace(matcher.group(0), "").trim();
                return OllamaResult.food(answer, safetyLevel);
            }

            // 안전 코드 파싱 실패 시 safetyLevel 없이 반환
            log.warn("AI 응답에서 안전 코드를 파싱하지 못했습니다.");
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
