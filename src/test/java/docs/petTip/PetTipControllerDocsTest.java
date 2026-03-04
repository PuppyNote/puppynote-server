package docs.petTip;

import com.puppynoteserver.petTip.controller.PetTipController;
import com.puppynoteserver.petTip.service.PetTipReadService;
import com.puppynoteserver.petTip.service.response.PetTipResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetTipControllerDocsTest extends RestDocsSupport {

    private final PetTipReadService petTipReadService = mock(PetTipReadService.class);

    @Override
    protected Object initController() {
        return new PetTipController(petTipReadService);
    }

    @DisplayName("랜덤 반려견 팁 조회 API")
    @Test
    void getRandomTip() throws Exception {
        PetTipResponse response = mock(PetTipResponse.class);
        given(response.getId()).willReturn(1L);
        given(response.getContent()).willReturn("강아지의 치아는 6개월마다 스케일링을 받는 것이 좋아요. 치주 질환은 심장, 신장 질환으로 이어질 수 있어요.");
        given(petTipReadService.getRandomTip()).willReturn(response);

        mockMvc.perform(
                        get("/api/v1/pet-tips/random")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-tip-random",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("팁 ID"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("팁 내용")
                        )
                ));
    }
}
