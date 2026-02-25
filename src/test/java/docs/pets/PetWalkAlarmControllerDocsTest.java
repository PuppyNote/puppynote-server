package docs.pets;

import com.puppynoteserver.pet.petWalkAlarms.controller.PetWalkAlarmController;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmCreateRequest;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmStatusUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmWriteService;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetWalkAlarmControllerDocsTest extends RestDocsSupport {

    private final PetWalkAlarmWriteService petWalkAlarmWriteService = mock(PetWalkAlarmWriteService.class);
    private final PetWalkAlarmReadService petWalkAlarmReadService = mock(PetWalkAlarmReadService.class);

    @Override
    protected Object initController() {
        return new PetWalkAlarmController(petWalkAlarmWriteService, petWalkAlarmReadService);
    }

    @DisplayName("산책 알람 등록 API")
    @Test
    void createAlarm() throws Exception {
        PetWalkAlarmCreateRequest request = PetWalkAlarmCreateRequest.builder()
                .petId(1L)
                .alarmStatus(AlarmStatus.YES)
                .alarmDays(Set.of(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI))
                .alarmTime(LocalTime.of(8, 0))
                .build();

        PetWalkAlarmResponse response = mock(PetWalkAlarmResponse.class);
        given(response.getAlarmId()).willReturn(1L);
        given(response.getAlarmStatus()).willReturn(AlarmStatus.YES);
        given(response.getAlarmDays()).willReturn(Set.of(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI));
        given(response.getAlarmTime()).willReturn(LocalTime.of(8, 0));
        given(petWalkAlarmWriteService.create(any(PetWalkAlarmCreateServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/v1/pet-walk-alarms")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("pet-alarm-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부. 가능한 값: " + Arrays.toString(AlarmStatus.values())),
                                fieldWithPath("alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일 (1개 이상 선택). 가능한 값: " + Arrays.toString(AlarmDay.values())),
                                fieldWithPath("alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm)")
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
                                fieldWithPath("data.alarmId").type(JsonFieldType.NUMBER)
                                        .description("알람 ID"),
                                fieldWithPath("data.alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부"),
                                fieldWithPath("data.alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일"),
                                fieldWithPath("data.alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm:ss)")
                        )
                ));
    }

    @DisplayName("산책 알람 수정 API")
    @Test
    void updateAlarm() throws Exception {
        PetWalkAlarmUpdateRequest request = PetWalkAlarmUpdateRequest.builder()
                .alarmId(1L)
                .alarmStatus(AlarmStatus.NO)
                .alarmDays(Set.of(AlarmDay.SAT, AlarmDay.SUN))
                .alarmTime(LocalTime.of(9, 30))
                .build();

        PetWalkAlarmResponse response = mock(PetWalkAlarmResponse.class);
        given(response.getAlarmId()).willReturn(1L);
        given(response.getAlarmStatus()).willReturn(AlarmStatus.NO);
        given(response.getAlarmDays()).willReturn(Set.of(AlarmDay.SAT, AlarmDay.SUN));
        given(response.getAlarmTime()).willReturn(LocalTime.of(9, 30));
        given(petWalkAlarmWriteService.update(any(PetWalkAlarmUpdateServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        put("/api/v1/pet-walk-alarms")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-alarm-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("alarmId").type(JsonFieldType.NUMBER)
                                        .description("수정할 알람 ID"),
                                fieldWithPath("alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부. 가능한 값: " + Arrays.toString(AlarmStatus.values())),
                                fieldWithPath("alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일 (1개 이상 선택). 가능한 값: " + Arrays.toString(AlarmDay.values())),
                                fieldWithPath("alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm)")
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
                                fieldWithPath("data.alarmId").type(JsonFieldType.NUMBER)
                                        .description("알람 ID"),
                                fieldWithPath("data.alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부"),
                                fieldWithPath("data.alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일"),
                                fieldWithPath("data.alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm:ss)")
                        )
                ));
    }

    @DisplayName("산책 알람 활성화 여부 수정 API")
    @Test
    void updateAlarmStatus() throws Exception {
        PetWalkAlarmStatusUpdateRequest request = PetWalkAlarmStatusUpdateRequest.builder()
                .alarmId(1L)
                .alarmStatus(AlarmStatus.NO)
                .build();

        PetWalkAlarmResponse response = mock(PetWalkAlarmResponse.class);
        given(response.getAlarmId()).willReturn(1L);
        given(response.getAlarmStatus()).willReturn(AlarmStatus.NO);
        given(response.getAlarmDays()).willReturn(Set.of(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI));
        given(response.getAlarmTime()).willReturn(LocalTime.of(8, 0));
        given(petWalkAlarmWriteService.updateStatus(any(PetWalkAlarmStatusUpdateServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        patch("/api/v1/pet-walk-alarms/status")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-alarm-update-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("alarmId").type(JsonFieldType.NUMBER)
                                        .description("수정할 알람 ID"),
                                fieldWithPath("alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부. 가능한 값: " + Arrays.toString(AlarmStatus.values()))
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
                                fieldWithPath("data.alarmId").type(JsonFieldType.NUMBER)
                                        .description("알람 ID"),
                                fieldWithPath("data.alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부"),
                                fieldWithPath("data.alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일"),
                                fieldWithPath("data.alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm:ss)")
                        )
                ));
    }

    @DisplayName("산책 알람 삭제 API")
    @Test
    void deleteAlarm() throws Exception {
        willDoNothing().given(petWalkAlarmWriteService).delete(anyLong());

        mockMvc.perform(
                        delete("/api/v1/pet-walk-alarms/{alarmId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-alarm-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("alarmId").description("삭제할 알람 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("응답 데이터 (없음)")
                        )
                ));
    }

    @DisplayName("산책 알람 목록 조회 API")
    @Test
    void getAlarms() throws Exception {
        PetWalkAlarmResponse response1 = mock(PetWalkAlarmResponse.class);
        given(response1.getAlarmId()).willReturn(1L);
        given(response1.getAlarmStatus()).willReturn(AlarmStatus.YES);
        given(response1.getAlarmDays()).willReturn(Set.of(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI));
        given(response1.getAlarmTime()).willReturn(LocalTime.of(8, 0));

        PetWalkAlarmResponse response2 = mock(PetWalkAlarmResponse.class);
        given(response2.getAlarmId()).willReturn(2L);
        given(response2.getAlarmStatus()).willReturn(AlarmStatus.NO);
        given(response2.getAlarmDays()).willReturn(Set.of(AlarmDay.SAT, AlarmDay.SUN));
        given(response2.getAlarmTime()).willReturn(LocalTime.of(9, 30));

        given(petWalkAlarmReadService.getAlarmsByPetId(anyLong()))
                .willReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/v1/pet-walk-alarms")
                                .param("petId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-alarm-list",
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
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].alarmId").type(JsonFieldType.NUMBER)
                                        .description("알람 ID"),
                                fieldWithPath("data[].alarmStatus").type(JsonFieldType.STRING)
                                        .description("알람 활성화 여부"),
                                fieldWithPath("data[].alarmDays").type(JsonFieldType.ARRAY)
                                        .description("알람 요일"),
                                fieldWithPath("data[].alarmTime").type(JsonFieldType.STRING)
                                        .description("알람 시간 (HH:mm:ss)")
                        )
                ));
    }
}
