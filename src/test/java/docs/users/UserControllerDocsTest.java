package docs.users;

import com.puppynoteserver.user.users.controller.UserController;
import com.puppynoteserver.user.users.controller.request.EmailSendRequest;
import com.puppynoteserver.user.users.controller.request.SignUpRequest;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.user.users.service.request.EmailSendServiceRequest;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

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

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
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
