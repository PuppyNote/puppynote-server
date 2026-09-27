package docs.alertSetting;

import com.puppynoteserver.alertSetting.controller.AlertSettingController;
import com.puppynoteserver.alertSetting.controller.request.AlertSettingUpdateRequest;
import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.alertSetting.service.AlertSettingService;
import com.puppynoteserver.alertSetting.service.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AlertSettingControllerDocsTest extends RestDocsSupport {

    private final AlertSettingService alertSettingService = mock(AlertSettingService.class);
    private final AlertSettingReadService alertSettingReadService = mock(AlertSettingReadService.class);

    @Override
    protected Object initController() {
        return new AlertSettingController(alertSettingService, alertSettingReadService);
    }

    @DisplayName("알림 설정 조회 API")
    @Test
    void getAlertSetting() throws Exception {
        given(alertSettingReadService.getAlertSetting())
                .willReturn(AlertSettingResponse.builder()
                        .all(AlertType.ON)
                        .walk(AlertType.ON)
                        .friend(AlertType.OFF)
                        .build());

        mockMvc.perform(
                        get("/api/v1/alert-setting")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("alert-setting-get",
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
                                fieldWithPath("data.all").type(JsonFieldType.STRING)
                                        .description("전체 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("data.walk").type(JsonFieldType.STRING)
                                        .description("산책 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("data.friend").type(JsonFieldType.STRING)
                                        .description("친구 알림 설정 " + Arrays.toString(AlertType.values()))
                        )
                ));
    }

    @DisplayName("알림 설정 수정 API")
    @Test
    void updateAlertSetting() throws Exception {
        AlertSettingUpdateRequest request = AlertSettingUpdateRequest.builder()
                .all(AlertType.ON)
                .walk(AlertType.OFF)
                .friend(AlertType.ON)
                .build();

        given(alertSettingService.updateAlertSetting(any(AlertSettingUpdateServiceRequest.class)))
                .willReturn(AlertSettingResponse.builder()
                        .all(AlertType.ON)
                        .walk(AlertType.OFF)
                        .friend(AlertType.ON)
                        .build());

        mockMvc.perform(
                        patch("/api/v1/alert-setting")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("alert-setting-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("all").type(JsonFieldType.STRING)
                                        .description("전체 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("walk").type(JsonFieldType.STRING)
                                        .description("산책 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("friend").type(JsonFieldType.STRING)
                                        .description("친구 알림 설정 " + Arrays.toString(AlertType.values()))
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
                                fieldWithPath("data.all").type(JsonFieldType.STRING)
                                        .description("전체 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("data.walk").type(JsonFieldType.STRING)
                                        .description("산책 알림 설정 " + Arrays.toString(AlertType.values())),
                                fieldWithPath("data.friend").type(JsonFieldType.STRING)
                                        .description("친구 알림 설정 " + Arrays.toString(AlertType.values()))
                        )
                ));
    }
}
