package docs.pets;

import com.puppynoteserver.pet.walk.controller.WalkController;
import com.puppynoteserver.pet.walk.controller.request.WalkCreateRequest;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.pet.walk.service.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalkControllerDocsTest extends RestDocsSupport {

    private final WalkWriteService walkWriteService = mock(WalkWriteService.class);

    @Override
    protected Object initController() {
        return new WalkController(walkWriteService);
    }

    @DisplayName("산책 이력 저장 API")
    @Test
    void createWalk() throws Exception {
        WalkCreateRequest request = WalkCreateRequest.builder()
                .petId(1L)
                .startTime(LocalDateTime.of(2026, 2, 25, 8, 0, 0))
                .endTime(LocalDateTime.of(2026, 2, 25, 8, 30, 0))
                .latitude(new BigDecimal("37.5665000"))
                .longitude(new BigDecimal("126.9780000"))
                .memo("오늘 산책 날씨 맑음")
                .photoKeys(List.of("walk/abc123.jpg", "walk/def456.jpg"))
                .build();

        WalkResponse response = mock(WalkResponse.class);
        given(response.getWalkId()).willReturn(1L);
        given(response.getPetId()).willReturn(1L);
        given(response.getStartTime()).willReturn(LocalDateTime.of(2026, 2, 25, 8, 0, 0));
        given(response.getEndTime()).willReturn(LocalDateTime.of(2026, 2, 25, 8, 30, 0));
        given(response.getLatitude()).willReturn(new BigDecimal("37.5665000"));
        given(response.getLongitude()).willReturn(new BigDecimal("126.9780000"));
        given(response.getMemo()).willReturn("오늘 산책 날씨 맑음");
        given(response.getPhotoUrls()).willReturn(List.of(
                "https://walk-photo.s3.ap-northeast-2.amazonaws.com/walk/abc123.jpg?X-Amz-Expires=3600",
                "https://walk-photo.s3.ap-northeast-2.amazonaws.com/walk/def456.jpg?X-Amz-Expires=3600"
        ));
        given(walkWriteService.create(any(WalkCreateServiceRequest.class))).willReturn(response);

        mockMvc.perform(
                        post("/api/v1/walks")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("walk-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("startTime").type(JsonFieldType.STRING)
                                        .description("산책 시작 시간 (yyyy-MM-dd'T'HH:mm:ss)"),
                                fieldWithPath("endTime").type(JsonFieldType.STRING)
                                        .description("산책 종료 시간 (yyyy-MM-dd'T'HH:mm:ss)"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("memo").type(JsonFieldType.STRING)
                                        .description("메모").optional(),
                                fieldWithPath("photoKeys").type(JsonFieldType.ARRAY)
                                        .description("산책 사진 S3 Object Key 목록 (Storage API 업로드 후 반환값)").optional()
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
                                fieldWithPath("data.walkId").type(JsonFieldType.NUMBER)
                                        .description("산책 ID"),
                                fieldWithPath("data.petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING)
                                        .description("산책 시작 시간"),
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING)
                                        .description("산책 종료 시간"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("data.memo").type(JsonFieldType.STRING)
                                        .description("메모").optional(),
                                fieldWithPath("data.photoUrls").type(JsonFieldType.ARRAY)
                                        .description("산책 사진 Presigned URL 목록 (유효시간 1시간)")
                        )
                ));
    }
}
