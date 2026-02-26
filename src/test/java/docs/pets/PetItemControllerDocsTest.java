package docs.pets;

import com.puppynoteserver.pet.petItems.controller.PetItemController;
import com.puppynoteserver.pet.petItems.controller.request.PetItemCreateRequest;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetItemControllerDocsTest extends RestDocsSupport {

    private final PetItemWriteService petItemWriteService = mock(PetItemWriteService.class);
    private final PetItemReadService petItemReadService = mock(PetItemReadService.class);

    @Override
    protected Object initController() {
        return new PetItemController(petItemWriteService, petItemReadService);
    }

    @DisplayName("용품 카테고리 공통코드 조회 API")
    @Test
    void getCategories() throws Exception {
        mockMvc.perform(
                        get("/api/v1/pet-items/categories")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-item-categories",
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
                                        .description("대분류별 카테고리 목록"),
                                fieldWithPath("data[].majorCategory").type(JsonFieldType.STRING)
                                        .description("대분류 코드"),
                                fieldWithPath("data[].majorCategoryName").type(JsonFieldType.STRING)
                                        .description("대분류명"),
                                fieldWithPath("data[].majorCategoryEmoji").type(JsonFieldType.STRING)
                                        .description("대분류 이모지"),
                                fieldWithPath("data[].categories").type(JsonFieldType.ARRAY)
                                        .description("소분류 목록"),
                                fieldWithPath("data[].categories[].category").type(JsonFieldType.STRING)
                                        .description("소분류 코드"),
                                fieldWithPath("data[].categories[].categoryName").type(JsonFieldType.STRING)
                                        .description("소분류명"),
                                fieldWithPath("data[].categories[].emoji").type(JsonFieldType.STRING)
                                        .description("소분류 이모지")
                        )
                ));
    }

    @DisplayName("용품 등록 API")
    @Test
    void createPetItem() throws Exception {
        PetItemCreateRequest request = PetItemCreateRequest.builder()
                .petId(1L)
                .name("로얄캐닌 미니 어덜트")
                .category(ItemCategory.FOOD)
                .purchaseCycleDays(30)
                .purchaseUrl("https://www.coupang.com/vp/products/example")
                .imageKey("pet-item-image/abc123.jpg")
                .build();

        PetItemResponse response = mock(PetItemResponse.class);
        given(response.getPetItemId()).willReturn(1L);
        given(response.getPetId()).willReturn(1L);
        given(response.getName()).willReturn("로얄캐닌 미니 어덜트");
        given(response.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response.getMajorCategoryName()).willReturn("식품/영양");
        given(response.getMajorCategoryEmoji()).willReturn("🍖");
        given(response.getCategory()).willReturn(ItemCategory.FOOD);
        given(response.getCategoryName()).willReturn("사료");
        given(response.getCategoryEmoji()).willReturn("🍚");
        given(response.getPurchaseCycleDays()).willReturn(30);
        given(response.getPurchaseUrl()).willReturn("https://www.coupang.com/vp/products/example");
        given(response.getImageUrl()).willReturn(
                "https://pet-item-image.s3.ap-northeast-2.amazonaws.com/abc123.jpg?X-Amz-Expires=3600"
        );
        given(response.getLastPurchasedAt()).willReturn(null);
        given(response.getNextPurchaseAt()).willReturn(null);
        given(petItemWriteService.create(any(PetItemCreateServiceRequest.class))).willReturn(response);

        mockMvc.perform(
                        post("/api/v1/pet-items")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("pet-item-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("용품명"),
                                fieldWithPath("category").type(JsonFieldType.STRING)
                                        .description("소분류 카테고리 코드 (공통코드 조회 API 참고)"),
                                fieldWithPath("purchaseCycleDays").type(JsonFieldType.NUMBER)
                                        .description("구매 주기 (일 단위, 최소 1)"),
                                fieldWithPath("purchaseUrl").type(JsonFieldType.STRING)
                                        .description("구매 링크 URL").optional(),
                                fieldWithPath("imageKey").type(JsonFieldType.STRING)
                                        .description("용품 이미지 S3 Object Key (Storage API 업로드 후 반환값)").optional()
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
                                fieldWithPath("data.petItemId").type(JsonFieldType.NUMBER)
                                        .description("용품 ID"),
                                fieldWithPath("data.petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("용품명"),
                                fieldWithPath("data.majorCategory").type(JsonFieldType.STRING)
                                        .description("대분류 코드"),
                                fieldWithPath("data.majorCategoryName").type(JsonFieldType.STRING)
                                        .description("대분류명"),
                                fieldWithPath("data.majorCategoryEmoji").type(JsonFieldType.STRING)
                                        .description("대분류 이모지"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING)
                                        .description("소분류 코드"),
                                fieldWithPath("data.categoryName").type(JsonFieldType.STRING)
                                        .description("소분류명"),
                                fieldWithPath("data.categoryEmoji").type(JsonFieldType.STRING)
                                        .description("소분류 이모지"),
                                fieldWithPath("data.purchaseCycleDays").type(JsonFieldType.NUMBER)
                                        .description("구매 주기 (일)"),
                                fieldWithPath("data.purchaseUrl").type(JsonFieldType.STRING)
                                        .description("구매 링크 URL").optional(),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                                        .description("용품 이미지 Presigned URL (유효시간 1시간)").optional(),
                                fieldWithPath("data.lastPurchasedAt").type(JsonFieldType.STRING)
                                        .description("최근 구매일 (yyyy-MM-dd)").optional(),
                                fieldWithPath("data.nextPurchaseAt").type(JsonFieldType.STRING)
                                        .description("다음 구매 예정일 (yyyy-MM-dd)").optional()
                        )
                ));
    }

    @DisplayName("용품 목록 조회 API")
    @Test
    void getPetItems() throws Exception {
        PetItemResponse response1 = mock(PetItemResponse.class);
        given(response1.getPetItemId()).willReturn(1L);
        given(response1.getPetId()).willReturn(1L);
        given(response1.getName()).willReturn("로얄캐닌 미니 어덜트");
        given(response1.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response1.getMajorCategoryName()).willReturn("식품/영양");
        given(response1.getMajorCategoryEmoji()).willReturn("🍖");
        given(response1.getCategory()).willReturn(ItemCategory.FOOD);
        given(response1.getCategoryName()).willReturn("사료");
        given(response1.getCategoryEmoji()).willReturn("🍚");
        given(response1.getPurchaseCycleDays()).willReturn(30);
        given(response1.getPurchaseUrl()).willReturn("https://www.coupang.com/vp/products/example");
        given(response1.getImageUrl()).willReturn(
                "https://pet-item-image.s3.ap-northeast-2.amazonaws.com/abc123.jpg?X-Amz-Expires=3600"
        );
        given(response1.getLastPurchasedAt()).willReturn(LocalDate.of(2026, 1, 25));
        given(response1.getNextPurchaseAt()).willReturn(LocalDate.of(2026, 2, 24));

        given(petItemReadService.getItemsByPetId(anyLong(), any())).willReturn(List.of(response1));

        mockMvc.perform(
                        get("/api/v1/pet-items")
                                .param("petId", "1")
                                .param("category", "FOOD")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-item-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("petId").description("조회할 펫 ID"),
                                parameterWithName("category").description("소분류 카테고리 필터 (선택, 미입력 시 전체 조회)").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("용품 목록 (다음 구매 예정일이 가까운 순으로 정렬, 미구매 항목 우선)"),
                                fieldWithPath("data[].petItemId").type(JsonFieldType.NUMBER)
                                        .description("용품 ID"),
                                fieldWithPath("data[].petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("용품명"),
                                fieldWithPath("data[].majorCategory").type(JsonFieldType.STRING)
                                        .description("대분류 코드"),
                                fieldWithPath("data[].majorCategoryName").type(JsonFieldType.STRING)
                                        .description("대분류명"),
                                fieldWithPath("data[].majorCategoryEmoji").type(JsonFieldType.STRING)
                                        .description("대분류 이모지"),
                                fieldWithPath("data[].category").type(JsonFieldType.STRING)
                                        .description("소분류 코드"),
                                fieldWithPath("data[].categoryName").type(JsonFieldType.STRING)
                                        .description("소분류명"),
                                fieldWithPath("data[].categoryEmoji").type(JsonFieldType.STRING)
                                        .description("소분류 이모지"),
                                fieldWithPath("data[].purchaseCycleDays").type(JsonFieldType.NUMBER)
                                        .description("구매 주기 (일)"),
                                fieldWithPath("data[].purchaseUrl").type(JsonFieldType.STRING)
                                        .description("구매 링크 URL").optional(),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .description("용품 이미지 Presigned URL (유효시간 1시간)").optional(),
                                fieldWithPath("data[].lastPurchasedAt").type(JsonFieldType.STRING)
                                        .description("최근 구매일 (yyyy-MM-dd)").optional(),
                                fieldWithPath("data[].nextPurchaseAt").type(JsonFieldType.STRING)
                                        .description("다음 구매 예정일 (yyyy-MM-dd)").optional()
                        )
                ));
    }

    @DisplayName("용품 상세 조회 API")
    @Test
    void getPetItemDetail() throws Exception {
        PetItemResponse response = mock(PetItemResponse.class);
        given(response.getPetItemId()).willReturn(1L);
        given(response.getPetId()).willReturn(1L);
        given(response.getName()).willReturn("로얄캐닌 미니 어덜트");
        given(response.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response.getMajorCategoryName()).willReturn("식품/영양");
        given(response.getMajorCategoryEmoji()).willReturn("🍖");
        given(response.getCategory()).willReturn(ItemCategory.FOOD);
        given(response.getCategoryName()).willReturn("사료");
        given(response.getCategoryEmoji()).willReturn("🍚");
        given(response.getPurchaseCycleDays()).willReturn(30);
        given(response.getPurchaseUrl()).willReturn("https://www.coupang.com/vp/products/example");
        given(response.getImageUrl()).willReturn(
                "https://pet-item-image.s3.ap-northeast-2.amazonaws.com/abc123.jpg?X-Amz-Expires=3600"
        );
        given(response.getLastPurchasedAt()).willReturn(LocalDate.of(2026, 1, 25));
        given(response.getNextPurchaseAt()).willReturn(LocalDate.of(2026, 2, 24));
        given(petItemReadService.getItemDetail(anyLong())).willReturn(response);

        mockMvc.perform(
                        get("/api/v1/pet-items/{petItemId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("pet-item-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("petItemId").description("용품 ID")
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
                                fieldWithPath("data.petItemId").type(JsonFieldType.NUMBER)
                                        .description("용품 ID"),
                                fieldWithPath("data.petId").type(JsonFieldType.NUMBER)
                                        .description("펫 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("용품명"),
                                fieldWithPath("data.majorCategory").type(JsonFieldType.STRING)
                                        .description("대분류 코드"),
                                fieldWithPath("data.majorCategoryName").type(JsonFieldType.STRING)
                                        .description("대분류명"),
                                fieldWithPath("data.majorCategoryEmoji").type(JsonFieldType.STRING)
                                        .description("대분류 이모지"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING)
                                        .description("소분류 코드"),
                                fieldWithPath("data.categoryName").type(JsonFieldType.STRING)
                                        .description("소분류명"),
                                fieldWithPath("data.categoryEmoji").type(JsonFieldType.STRING)
                                        .description("소분류 이모지"),
                                fieldWithPath("data.purchaseCycleDays").type(JsonFieldType.NUMBER)
                                        .description("구매 주기 (일)"),
                                fieldWithPath("data.purchaseUrl").type(JsonFieldType.STRING)
                                        .description("구매 링크 URL").optional(),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                                        .description("용품 이미지 Presigned URL (유효시간 1시간)").optional(),
                                fieldWithPath("data.lastPurchasedAt").type(JsonFieldType.STRING)
                                        .description("최근 구매일 (yyyy-MM-dd)").optional(),
                                fieldWithPath("data.nextPurchaseAt").type(JsonFieldType.STRING)
                                        .description("다음 구매 예정일 (yyyy-MM-dd)").optional()
                        )
                ));
    }
}
