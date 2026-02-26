package docs.users;

import com.puppynoteserver.user.userItemCategories.controller.UserItemCategoryController;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryReadService;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryWriteService;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

public class UserItemCategoryControllerDocsTest extends RestDocsSupport {

    private final UserItemCategoryWriteService userItemCategoryWriteService = mock(UserItemCategoryWriteService.class);
    private final UserItemCategoryReadService userItemCategoryReadService = mock(UserItemCategoryReadService.class);

    @Override
    protected Object initController() {
        return new UserItemCategoryController(userItemCategoryWriteService, userItemCategoryReadService);
    }

    @DisplayName("사용자 카테고리 저장 API")
    @Test
    void saveCategories() throws Exception {
        String requestBody = """
                {
                  "categoryType": "ITEM",
                  "categories": ["FOOD", "SNACK", "TOY", "SHAMPOO"]
                }
                """;

        UserItemCategoryResponse response1 = mock(UserItemCategoryResponse.class);
        given(response1.getUserItemCategoryId()).willReturn(1L);
        given(response1.getCategoryType()).willReturn("ITEM");
        given(response1.getCategoryTypeDescription()).willReturn("용품");
        given(response1.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response1.getMajorCategoryName()).willReturn("식품/영양");
        given(response1.getMajorCategoryEmoji()).willReturn("🍖");
        given(response1.getCategory()).willReturn("FOOD");
        given(response1.getCategoryName()).willReturn("사료");
        given(response1.getCategoryEmoji()).willReturn("🍚");
        given(response1.getSort()).willReturn(1);

        UserItemCategoryResponse response2 = mock(UserItemCategoryResponse.class);
        given(response2.getUserItemCategoryId()).willReturn(2L);
        given(response2.getCategoryType()).willReturn("ITEM");
        given(response2.getCategoryTypeDescription()).willReturn("용품");
        given(response2.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response2.getMajorCategoryName()).willReturn("식품/영양");
        given(response2.getMajorCategoryEmoji()).willReturn("🍖");
        given(response2.getCategory()).willReturn("SNACK");
        given(response2.getCategoryName()).willReturn("간식");
        given(response2.getCategoryEmoji()).willReturn("🦴");
        given(response2.getSort()).willReturn(2);

        given(userItemCategoryWriteService.saveCategories(any(CategoryType.class), anyList()))
                .willReturn(List.of(response1, response2));

        mockMvc.perform(
                        post("/api/v1/user-item-categories")
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-item-category-save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("categoryType").type(JsonFieldType.STRING)
                                        .description("카테고리 타입 (ITEM: 용품, ACTIVITY: 활동)"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                                        .description("저장할 카테고리 코드 목록 (배열 순서가 정렬 순서가 됩니다)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("저장된 카테고리 목록"),
                                fieldWithPath("data[].userItemCategoryId").type(JsonFieldType.NUMBER)
                                        .description("사용자 카테고리 ID"),
                                fieldWithPath("data[].categoryType").type(JsonFieldType.STRING)
                                        .description("카테고리 타입 코드 (ITEM / ACTIVITY)"),
                                fieldWithPath("data[].categoryTypeDescription").type(JsonFieldType.STRING)
                                        .description("카테고리 타입명"),
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
                                fieldWithPath("data[].sort").type(JsonFieldType.NUMBER)
                                        .description("정렬 순서 (1부터 시작)")
                        )
                ));
    }

    @DisplayName("사용자 카테고리 목록 조회 API")
    @Test
    void getMyCategories() throws Exception {
        UserItemCategoryResponse response1 = mock(UserItemCategoryResponse.class);
        given(response1.getUserItemCategoryId()).willReturn(1L);
        given(response1.getCategoryType()).willReturn("ITEM");
        given(response1.getCategoryTypeDescription()).willReturn("용품");
        given(response1.getMajorCategory()).willReturn("FOOD_NUTRITION");
        given(response1.getMajorCategoryName()).willReturn("식품/영양");
        given(response1.getMajorCategoryEmoji()).willReturn("🍖");
        given(response1.getCategory()).willReturn("FOOD");
        given(response1.getCategoryName()).willReturn("사료");
        given(response1.getCategoryEmoji()).willReturn("🍚");
        given(response1.getSort()).willReturn(1);

        UserItemCategoryResponse response2 = mock(UserItemCategoryResponse.class);
        given(response2.getUserItemCategoryId()).willReturn(2L);
        given(response2.getCategoryType()).willReturn("ITEM");
        given(response2.getCategoryTypeDescription()).willReturn("용품");
        given(response2.getMajorCategory()).willReturn("PLAY");
        given(response2.getMajorCategoryName()).willReturn("장난감/훈련");
        given(response2.getMajorCategoryEmoji()).willReturn("🎾");
        given(response2.getCategory()).willReturn("TOY");
        given(response2.getCategoryName()).willReturn("장난감");
        given(response2.getCategoryEmoji()).willReturn("🎾");
        given(response2.getSort()).willReturn(2);

        given(userItemCategoryReadService.getMyCategories(any(CategoryType.class)))
                .willReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/v1/user-item-categories")
                                .param("categoryType", "ITEM")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-item-category-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("categoryType").description("카테고리 타입 (ITEM: 용품, ACTIVITY: 활동)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("사용자 카테고리 목록 (sort 순으로 정렬)"),
                                fieldWithPath("data[].userItemCategoryId").type(JsonFieldType.NUMBER)
                                        .description("사용자 카테고리 ID"),
                                fieldWithPath("data[].categoryType").type(JsonFieldType.STRING)
                                        .description("카테고리 타입 코드 (ITEM / ACTIVITY)"),
                                fieldWithPath("data[].categoryTypeDescription").type(JsonFieldType.STRING)
                                        .description("카테고리 타입명"),
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
                                fieldWithPath("data[].sort").type(JsonFieldType.NUMBER)
                                        .description("정렬 순서 (1부터 시작)")
                        )
                ));
    }
}
