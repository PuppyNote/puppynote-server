package docs.pets;

import com.puppynoteserver.pet.familyMembers.controller.FamilyMemberController;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberWriteService;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FamilyMemberControllerDocsTest extends RestDocsSupport {

    private final FamilyMemberReadService familyMemberReadService = mock(FamilyMemberReadService.class);
    private final FamilyMemberWriteService familyMemberWriteService = mock(FamilyMemberWriteService.class);

    @Override
    protected Object initController() {
        return new FamilyMemberController(familyMemberReadService, familyMemberWriteService);
    }

    @DisplayName("가족 목록 조회 API")
    @Test
    void getFamilyMembers() throws Exception {
        FamilyMemberResponse response1 = mock(FamilyMemberResponse.class);
        given(response1.getUserId()).willReturn(1L);
        given(response1.getNickName()).willReturn("뽀삐아빠");
        given(response1.getProfileUrl()).willReturn("https://example.com/profile1.jpg");
        given(response1.getRole()).willReturn(RoleType.OWNER);
        given(response1.getStatus()).willReturn(FamilyMemberStatus.DONE);

        FamilyMemberResponse response2 = mock(FamilyMemberResponse.class);
        given(response2.getUserId()).willReturn(2L);
        given(response2.getNickName()).willReturn("뽀삐엄마");
        given(response2.getProfileUrl()).willReturn("https://example.com/profile2.jpg");
        given(response2.getRole()).willReturn(RoleType.FAMILY);
        given(response2.getStatus()).willReturn(FamilyMemberStatus.DONE);

        given(familyMemberReadService.getFamilyMembers()).willReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/v1/family-members")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("family-member-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("가족 목록 (등록 완료된 가족)"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("data[].nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data[].profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                                fieldWithPath("data[].role").type(JsonFieldType.STRING).description("역할 (OWNER: 주인, FAMILY: 가족)"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태 (DONE: 등록 완료)")
                        )
                ));
    }

    @DisplayName("유저 검색 API (이메일 LIKE)")
    @Test
    void searchUsers() throws Exception {
        UserSearchResponse response1 = mock(UserSearchResponse.class);
        given(response1.getUserId()).willReturn(2L);
        given(response1.getEmail()).willReturn("puppy@example.com");
        given(response1.getNickName()).willReturn("뽀삐엄마");
        given(response1.getProfileUrl()).willReturn("https://example.com/profile2.jpg");

        UserSearchResponse response2 = mock(UserSearchResponse.class);
        given(response2.getUserId()).willReturn(3L);
        given(response2.getEmail()).willReturn("puppylover@example.com");
        given(response2.getNickName()).willReturn("강아지사랑");
        given(response2.getProfileUrl()).willReturn(null);

        given(familyMemberReadService.searchUsersByEmail(anyString())).willReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/v1/family-members/search")
                                .param("email", "puppy")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("family-member-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("email").description("검색할 이메일 키워드 (LIKE 검색)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("검색된 유저 목록"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data[].profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional()
                        )
                ));
    }

    @DisplayName("가족 초대 API")
    @Test
    void invite() throws Exception {
        doNothing().when(familyMemberWriteService).invite(any(FamilyMemberInviteServiceRequest.class));

        mockMvc.perform(
                        post("/api/v1/family-members/invite")
                                .content("{\"inviteeUserId\": 2}")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("family-member-invite",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("inviteeUserId").type(JsonFieldType.NUMBER)
                                        .description("초대할 유저 ID. 초대 시 내 모든 강아지에 대해 가족 관계가 생성되며 Push 알림이 발송됩니다.")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음")
                        )
                ));
    }

    @DisplayName("가족 등록 API")
    @Test
    void register() throws Exception {
        doNothing().when(familyMemberWriteService).register(any(FamilyMemberRegisterServiceRequest.class));

        mockMvc.perform(
                        post("/api/v1/family-members/register")
                                .content("{\"inviterUserId\": 1}")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("family-member-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("inviterUserId").type(JsonFieldType.NUMBER)
                                        .description("초대한 유저 ID. 해당 유저의 모든 강아지에 대해 PENDING → DONE으로 변경됩니다.")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음")
                        )
                ));
    }
}
