package docs.home;

import com.puppynoteserver.home.controller.HomeController;
import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.home.service.response.HomeResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
                .willReturn(HomeResponse.builder()
                        .petName("뽀삐")
                        .petProfileImageUrl("https://puppy-profile.s3.ap-northeast-2.amazonaws.com/profile/abc123.jpg?X-Amz-Expires=3600")
                        .birthDate(LocalDate.of(2021, 3, 15))
                        .petAge("3살")
                        .birthdayDday(12)
                        .walkedToday(true)
                        .daysSinceLastWalk(0)
                        .monthlyWalkMinutes(320L)
                        .recentWalkCount(5L)
                        .petItemCount(12L)
                        .todayWalkAlarmTimes(List.of(LocalTime.of(8, 0), LocalTime.of(18, 30)))
                        .build());

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
                                fieldWithPath("data.birthDate").type(JsonFieldType.STRING)
                                        .description("반려견 생일 (yyyy-MM-dd). 생일 미등록 시 null").optional(),
                                fieldWithPath("data.petAge").type(JsonFieldType.STRING)
                                        .description("펫 나이 (예: '3살', '8개월'). 생일 미등록 시 null").optional(),
                                fieldWithPath("data.birthdayDday").type(JsonFieldType.NUMBER)
                                        .description("생일까지 남은 일수 (0 = 오늘 생일). 생일 미등록 시 null").optional(),
                                fieldWithPath("data.walkedToday").type(JsonFieldType.BOOLEAN)
                                        .description("오늘 산책 여부"),
                                fieldWithPath("data.daysSinceLastWalk").type(JsonFieldType.NUMBER)
                                        .description("마지막 산책 후 경과 일수 (산책 이력 없으면 null)").optional(),
                                fieldWithPath("data.monthlyWalkMinutes").type(JsonFieldType.NUMBER)
                                        .description("이번 달 총 산책 시간 (분)"),
                                fieldWithPath("data.recentWalkCount").type(JsonFieldType.NUMBER)
                                        .description("최근 7일 산책 횟수"),
                                fieldWithPath("data.petItemCount").type(JsonFieldType.NUMBER)
                                        .description("관리용품 개수"),
                                fieldWithPath("data.todayWalkAlarmTimes").type(JsonFieldType.ARRAY)
                                        .description("오늘 설정된 산책 알람 시간 목록 (HH:mm:ss)")
                        )
                ));
    }
}
