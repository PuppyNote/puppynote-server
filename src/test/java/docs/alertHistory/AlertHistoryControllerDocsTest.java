package docs.alertHistory;

import com.puppynoteserver.alertHistory.controller.alertHistory.AlertHistoryController;
import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.alertHistory.entity.AlertHistoryStatus;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryReadService;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryResponse;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryStatusResponse;
import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.global.page.response.PageableCustom;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AlertHistoryControllerDocsTest extends RestDocsSupport {

    private final AlertHistoryService alertHistoryService = mock(AlertHistoryService.class);
    private final AlertHistoryReadService alertHistoryReadService = mock(AlertHistoryReadService.class);

    @Override
    protected Object initController() {
        return new AlertHistoryController(alertHistoryService, alertHistoryReadService);
    }

    @DisplayName("알림 내역 목록 조회 API")
    @Test
    void getAlertHistory() throws Exception {
        AlertHistoryResponse response = AlertHistoryResponse.builder()
                .id(1L)
                .alertDescription("친구 요청이 도착했습니다.")
                .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND_CODE)
                .alertDestinationInfo("ABC123")
                .createdDate(LocalDateTime.of(2026, 2, 27, 10, 0, 0))
                .build();

        PageCustom<AlertHistoryResponse> pageCustom = PageCustom.<AlertHistoryResponse>builder()
                .content(List.of(response))
                .pageInfo(PageableCustom.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalElement(1)
                        .build())
                .build();

        given(alertHistoryReadService.getAlertHistory(any(PageInfoServiceRequest.class)))
                .willReturn(pageCustom);

        mockMvc.perform(
                        get("/api/v1/alertHistories")
                                .param("page", "1")
                                .param("size", "12")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("alert-history-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (기본값: 1)").optional(),
                                parameterWithName("size").description("페이지 크기 (기본값: 12)").optional()
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
                                fieldWithPath("data.content").type(JsonFieldType.ARRAY)
                                        .description("알림 내역 목록"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                        .description("알림 내역 ID"),
                                fieldWithPath("data.content[].alertDescription").type(JsonFieldType.STRING)
                                        .description("알림 내용"),
                                fieldWithPath("data.content[].alertHistoryStatus").type(JsonFieldType.STRING)
                                        .description("알림 확인 상태 " + Arrays.toString(AlertHistoryStatus.values())),
                                fieldWithPath("data.content[].alertDestinationType").type(JsonFieldType.STRING)
                                        .description("알림 목적지 타입 " + Arrays.toString(AlertDestinationType.values())),
                                fieldWithPath("data.content[].alertDestinationInfo").type(JsonFieldType.STRING)
                                        .description("알림 목적지 정보"),
                                fieldWithPath("data.content[].createdDate").type(JsonFieldType.STRING)
                                        .description("알림 생성 일시"),
                                fieldWithPath("data.pageInfo").type(JsonFieldType.OBJECT)
                                        .description("페이지 정보"),
                                fieldWithPath("data.pageInfo.currentPage").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지"),
                                fieldWithPath("data.pageInfo.totalPage").type(JsonFieldType.NUMBER)
                                        .description("전체 페이지 수"),
                                fieldWithPath("data.pageInfo.totalElement").type(JsonFieldType.NUMBER)
                                        .description("전체 데이터 수")
                        )
                ));
    }

    @DisplayName("알림 확인 처리 API")
    @Test
    void updateAlertHistoryStatus() throws Exception {
        given(alertHistoryService.updateAlertHistoryStatus(anyLong()))
                .willReturn(AlertHistoryStatusResponse.of(AlertHistoryStatus.CHECKED));

        mockMvc.perform(
                        patch("/api/v1/alertHistories/{id}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("alert-history-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("확인 처리할 알림 내역 ID")
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
                                fieldWithPath("data.alertHistoryStatus").type(JsonFieldType.STRING)
                                        .description("변경된 알림 확인 상태 " + Arrays.toString(AlertHistoryStatus.values()))
                        )
                ));
    }
}
