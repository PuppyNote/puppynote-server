package docs.pets;

import com.puppynoteserver.pet.petItemPurchase.controller.PetItemPurchaseController;
import com.puppynoteserver.pet.petItemPurchase.controller.request.PetItemPurchaseCreateRequest;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetItemPurchaseControllerDocsTest extends RestDocsSupport {

    private final PetItemPurchaseWriteService petItemPurchaseWriteService = mock(PetItemPurchaseWriteService.class);

    @Override
    protected Object initController() {
        return new PetItemPurchaseController(petItemPurchaseWriteService);
    }

    @DisplayName("용품 구매 이력 등록 API")
    @Test
    void recordPurchase() throws Exception {
        PetItemPurchaseCreateRequest request = new PetItemPurchaseCreateRequest();

        PetItemPurchaseResponse response = mock(PetItemPurchaseResponse.class);
        given(response.getId()).willReturn(1L);
        given(response.getPetItemId()).willReturn(1L);
        given(response.getPurchasedAt()).willReturn(LocalDate.of(2026, 3, 4));
        given(petItemPurchaseWriteService.recordPurchase(any(PetItemPurchaseCreateServiceRequest.class))).willReturn(response);

        mockMvc.perform(
                        post("/api/v1/pet-items/{petItemId}/purchases", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("pet-item-purchase-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("petItemId").description("용품 ID")
                        ),
                        requestFields(
                                fieldWithPath("purchasedAt").type(JsonFieldType.STRING)
                                        .description("구매일 (yyyy-MM-dd, 미입력 시 오늘 날짜로 처리)").optional()
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
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("구매 이력 ID"),
                                fieldWithPath("data.petItemId").type(JsonFieldType.NUMBER)
                                        .description("용품 ID"),
                                fieldWithPath("data.purchasedAt").type(JsonFieldType.STRING)
                                        .description("구매일 (yyyy-MM-dd)")
                        )
                ));
    }
}
