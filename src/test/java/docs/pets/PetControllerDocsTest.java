package docs.pets;

import com.puppynoteserver.pet.pets.controller.PetController;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetControllerDocsTest extends RestDocsSupport {

    private final PetReadService petReadService = mock(PetReadService.class);

    @Override
    protected Object initController() {
        return new PetController(petReadService);
    }

    @DisplayName("내 펫 목록 조회 API")
    @Test
    void getMyPets() throws Exception {
        PetResponse petResponse = mock(PetResponse.class);
        given(petResponse.getPetId()).willReturn(1L);
        given(petResponse.getPetName()).willReturn("초코");

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
                                        .description("펫 이름")
                        )
                ));
    }
}
