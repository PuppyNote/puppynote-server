package docs.users;

import com.puppynoteserver.user.push.controller.DeviceTokenController;
import com.puppynoteserver.user.push.service.PushWriteService;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeviceTokenControllerDocsTest extends RestDocsSupport {

    private final PushWriteService pushWriteService = mock(PushWriteService.class);

    @Override
    protected Object initController() {
        return new DeviceTokenController(pushWriteService);
    }

    @DisplayName("디바이스 토큰 등록 API")
    @Test
    void registerDeviceToken() throws Exception {
        String requestBody = """
                {
                  "deviceId": "b3f1c9d2-4e5a-4f6b-8c7d-9e0a1b2c3d4e",
                  "pushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
                  "platform": "WEB"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .header("Authorization", "Bearer {accessToken}")
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("device-token-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer {accessToken}")
                        ),
                        requestFields(
                                fieldWithPath("deviceId").type(JsonFieldType.STRING)
                                        .description("디바이스 고유 ID (등록/갱신 기준 키)"),
                                fieldWithPath("pushToken").type(JsonFieldType.STRING)
                                        .description("푸시 디바이스 토큰"),
                                fieldWithPath("platform").type(JsonFieldType.STRING)
                                        .description("플랫폼 (IOS / ANDROID / WEB, 대소문자 무관)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("응답 데이터 없음")
                        )
                ));
    }
}
