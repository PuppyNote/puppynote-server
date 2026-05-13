package docs.foodChat;

import com.puppynoteserver.foodChat.controller.FoodChatController;
import com.puppynoteserver.foodChat.entity.FoodChatHistory.SafetyLevel;
import com.puppynoteserver.foodChat.service.FoodChatService;
import com.puppynoteserver.foodChat.service.FoodSearchService;
import com.puppynoteserver.foodChat.service.response.FoodListResponse;
import com.puppynoteserver.foodChat.service.response.FoodResponse;
import com.puppynoteserver.global.exception.PuppyNoteException;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FoodControllerDocsTest extends RestDocsSupport {

    private final FoodChatService foodChatService = mock(FoodChatService.class);
    private final FoodSearchService foodSearchService = mock(FoodSearchService.class);

    @Override
    protected Object initController() {
        return new FoodChatController(foodChatService, foodSearchService);
    }

    @DisplayName("음식 조회 API - ES 검색")
    @Test
    void searchFood() throws Exception {
        FoodResponse foodResponse = mock(FoodResponse.class);
        given(foodResponse.getId()).willReturn(1L);
        given(foodResponse.getQuestion()).willReturn("당근은 강아지에게 먹여도 되나요?");
        given(foodResponse.getAnswer()).willReturn("당근은 강아지에게 안전한 채소입니다. 비타민 A가 풍부하며 소량 급여 시 건강에 도움이 됩니다.");
        given(foodResponse.getSafetyLevel()).willReturn(SafetyLevel.GOOD);

        given(foodChatService.search(any())).willReturn(FoodListResponse.of(List.of(foodResponse), 0, 1L));

        mockMvc.perform(
                        get("/api/v1/foods")
                                .param("question", "당근은 강아지에게 먹여도 되나요?")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("food-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("question").description("음식 관련 질문 (필수)"),
                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                                parameterWithName("size").description("페이지 크기 (기본값: 10)").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("음식 정보 목록 (ES 결과 없으면 빈 배열)"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("음식 정보 ID"),
                                fieldWithPath("data.content[].question").type(JsonFieldType.STRING).description("질문"),
                                fieldWithPath("data.content[].answer").type(JsonFieldType.STRING).description("AI 답변"),
                                fieldWithPath("data.content[].safetyLevel").type(JsonFieldType.STRING)
                                        .description("안전 등급 (GOOD: 안전, NOTION: 주의, BAD: 위험)"),
                                fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("전체 결과 수")
                        )
                ));
    }

    @DisplayName("음식 AI 조회 API - 정상")
    @Test
    void askFood() throws Exception {
        FoodResponse foodResponse = mock(FoodResponse.class);
        given(foodResponse.getId()).willReturn(2L);
        given(foodResponse.getQuestion()).willReturn("포도는 강아지에게 줘도 되나요?");
        given(foodResponse.getAnswer()).willReturn("포도는 강아지에게 절대 급여하면 안 됩니다. 신장 손상을 유발할 수 있으며 심각한 경우 사망에 이를 수 있습니다.");
        given(foodResponse.getSafetyLevel()).willReturn(SafetyLevel.BAD);

        given(foodChatService.ask(any())).willReturn(foodResponse);

        mockMvc.perform(
                        post("/api/v1/foods/ai")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(new FoodAskRequestDoc("포도는 강아지에게 줘도 되나요?")))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("food-ask-ai",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("question").type(JsonFieldType.STRING).description("음식 관련 질문 (필수)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("음식 정보 ID"),
                                fieldWithPath("data.question").type(JsonFieldType.STRING).description("질문"),
                                fieldWithPath("data.answer").type(JsonFieldType.STRING).description("AI 답변"),
                                fieldWithPath("data.safetyLevel").type(JsonFieldType.STRING)
                                        .description("안전 등급 (GOOD: 안전, NOTION: 주의, BAD: 위험)")
                        )
                ));
    }

    static class FoodAskRequestDoc {
        public final String question;

        FoodAskRequestDoc(String question) {
            this.question = question;
        }
    }
}
