package docs.pets;

import com.puppynoteserver.pet.pets.controller.PetController;
import com.puppynoteserver.pet.pets.controller.request.PetCreateRequest;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetControllerDocsTest extends RestDocsSupport {

    private final PetReadService petReadService = mock(PetReadService.class);
    private final PetWriteService petWriteService = mock(PetWriteService.class);

    @Override
    protected Object initController() {
        return new PetController(petReadService, petWriteService);
    }

    @DisplayName("내 펫 목록 조회 API")
    @Test
    void getMyPets() throws Exception {
        PetResponse petResponse = mock(PetResponse.class);
        given(petResponse.getPetId()).willReturn(1L);
        given(petResponse.getPetName()).willReturn("초코");
        given(petResponse.getPetProfileUrl()).willReturn(
                "https://puppy-profile.s3.ap-northeast-2.amazonaws.com/550e8400.jpg?X-Amz-Expires=3600"
        );

        given(petReadService.getMyPets())
                .willReturn(List.of(petResponse));

        mockMvc.perform(
                        get("/api/v1/pets")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("data[].petName").type(JsonFieldType.STRING)
                                        .description("펫 이름"),
                                fieldWithPath("data[].petProfileUrl").type(JsonFieldType.STRING)
                                        .description("펫 프로필 이미지 Presigned URL (유효시간 1시간)").optional()
                        )
                ));
    }

    @DisplayName("펫 등록 API")
    @Test
    void createPet() throws Exception {
        PetCreateRequest request = PetCreateRequest.builder()
                .name("초코")
                .breed("포메라니안")
                .birthDate(LocalDate.of(2022, 3, 15))
                .weight(new BigDecimal("3.50"))
                .profileImageUrl("550e8400-e29b-41d4-a716-446655440000.jpg")
                .build();

        given(petWriteService.createPet(any(PetCreateServiceRequest.class)))
                .willReturn(mock(PetCreateResponse.class));

        PetCreateResponse response = mock(PetCreateResponse.class);
        given(response.getPetId()).willReturn(1L);
        given(response.getPetName()).willReturn("초코");
        given(petWriteService.createPet(any(PetCreateServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/v1/pets")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("pet-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("펫 이름 (필수)"),
                                fieldWithPath("breed").type(JsonFieldType.STRING)
                                        .description("품종").optional(),
                                fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                        .description("생년월일 (yyyy-MM-dd)").optional(),
                                fieldWithPath("weight").type(JsonFieldType.NUMBER)
                                        .description("몸무게 (kg)").optional(),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 S3 Object Key (Storage API 업로드 후 반환값)").optional()
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
                                fieldWithPath("data.petId").type(JsonFieldType.NUMBER)
                                        .description("등록된 펫 ID"),
                                fieldWithPath("data.petName").type(JsonFieldType.STRING)
                                        .description("등록된 펫 이름")
                        )
                ));
    }
}
