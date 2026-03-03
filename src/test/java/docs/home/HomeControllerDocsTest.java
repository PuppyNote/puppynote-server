package docs.home;

import com.puppynoteserver.home.controller.HomeController;
import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.home.service.response.HomeResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HomeControllerDocsTest extends RestDocsSupport {

    private final HomeReadService homeReadService = mock(HomeReadService.class);

    @Override
    protected Object initController() {
        return new HomeController(homeReadService);
    }

    @DisplayName("메인화면 기본정보 조회 API")
    @Test
    void getHomeInfo() throws Exception {
        given(homeReadService.getHomeInfo(anyLong()))
                .willReturn(HomeResponse.of(
                        "뽀삐",
                        "https://puppy-profile.s3.ap-northeast-2.amazonaws.com/profile/abc123.jpg?X-Amz-Expires=3600",
                        5L,
                        12L
                ));

        mockMvc.perform(
                        get("/api/v1/home")
                                .param("petId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("home-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("petId").description("조회할 펫 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.petName").type(JsonFieldType.STRING)
                                        .description("펫 이름"),
                                fieldWithPath("data.petProfileImageUrl").type(JsonFieldType.STRING)
                                        .description("펫 프로필 사진 Presigned URL (유효시간 1시간)").optional(),
                                fieldWithPath("data.recentWalkCount").type(JsonFieldType.NUMBER)
                                        .description("최근 7일 산책 횟수"),
                                fieldWithPath("data.petItemCount").type(JsonFieldType.NUMBER)
                                        .description("관리용품 개수")
                        )
                ));
    }
}
