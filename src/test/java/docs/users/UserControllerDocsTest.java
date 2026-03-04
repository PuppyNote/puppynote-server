package docs.users;

import com.puppynoteserver.user.users.controller.UserController;
import com.puppynoteserver.user.users.controller.request.EmailSendRequest;
import com.puppynoteserver.user.users.controller.request.SignUpRequest;
import com.puppynoteserver.user.users.controller.request.UserProfileUpdateRequest;
import com.puppynoteserver.user.users.service.UserReadService;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.user.users.service.request.EmailSendServiceRequest;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.request.UserProfileUpdateServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import com.puppynoteserver.user.users.service.response.UserProfileResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final UserReadService userReadService = mock(UserReadService.class);

    @Override
    protected Object initController() {
        return new UserController(userService, userReadService);
    }

    @DisplayName("회원가입 API")
    @Test
    void signUp() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("test@puppynote.com")
                .password("password123")
                .nickName("멍멍이주인")
                .build();

        given(userService.signUp(any(SignUpServiceRequest.class)))
                .willReturn(SignUpResponse.builder()
                        .email("test@puppynote.com")
                        .nickName("멍멍이주인")
                        .build());

        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("user-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호 (최소 8자)"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING)
                                        .description("닉네임 (최대 20자)")
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
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING)
                                        .description("닉네임")
                        )
                ));
    }

    @DisplayName("내 프로필 조회 API")
    @Test
    void getMyProfile() throws Exception {
        UserProfileResponse response = mock(UserProfileResponse.class);
        given(response.getEmail()).willReturn("test@puppynote.com");
        given(response.getNickName()).willReturn("멍멍이주인");
        given(response.getProfileUrl()).willReturn("https://example.com/profile.jpg");
        given(userReadService.getMyProfile()).willReturn(response);

        mockMvc.perform(
                        get("/api/v1/user/profile")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-profile-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional()
                        )
                ));
    }

    @DisplayName("내 프로필 수정 API")
    @Test
    void updateProfile() throws Exception {
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .nickName("새닉네임")
                .profileUrl("https://example.com/new-profile.jpg")
                .build();

        doNothing().when(userService).updateProfile(any(UserProfileUpdateServiceRequest.class));

        mockMvc.perform(
                        patch("/api/v1/user/profile")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-profile-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임 (필수)"),
                                fieldWithPath("profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음")
                        )
                ));
    }

    @DisplayName("이메일 인증번호 발송 API")
    @Test
    void sendVerificationEmail() throws Exception {
        EmailSendRequest request = EmailSendRequest.builder()
                .email("test@puppynote.com")
                .build();

        given(userService.sendVerificationEmail(any(EmailSendServiceRequest.class)))
                .willReturn("382910");

        mockMvc.perform(
                        post("/api/v1/user/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-email-send",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("인증번호를 받을 이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("발송된 인증번호 6자리")
                        )
                ));
    }

}
