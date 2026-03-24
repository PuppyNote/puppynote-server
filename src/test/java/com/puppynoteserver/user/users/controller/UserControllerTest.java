package com.puppynoteserver.user.users.controller;

import com.puppynoteserver.ControllerTestSupport;
import com.puppynoteserver.user.users.controller.request.EmailSendRequest;
import com.puppynoteserver.user.users.controller.request.SignUpRequest;
import com.puppynoteserver.user.users.controller.request.UserProfileUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends ControllerTestSupport {

    // ===== signUp =====

    @DisplayName("회원가입을 한다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUp() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickName("테스트닉네임")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("회원가입 시 이메일은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithoutEmail() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .password("password123")
                .nickName("테스트닉네임")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("이메일은 필수입니다."));
    }

    @DisplayName("회원가입 시 이메일 형식이 올바르지 않으면 안된다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithInvalidEmail() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("not-an-email")
                .password("password123")
                .nickName("테스트닉네임")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다."));
    }

    @DisplayName("회원가입 시 비밀번호는 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithoutPassword() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .nickName("테스트닉네임")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수입니다."));
    }

    @DisplayName("회원가입 시 비밀번호는 최소 8자 이상이어야 한다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithTooShortPassword() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("short")
                .nickName("테스트닉네임")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자 이상이어야 합니다."));
    }

    @DisplayName("회원가입 시 닉네임은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithoutNickName() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("닉네임은 필수입니다."));
    }

    @DisplayName("회원가입 시 닉네임은 최대 20자까지 입력 가능하다.")
    @Test
    @WithMockUser(roles = "USER")
    void signUpWithTooLongNickName() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickName("이닉네임은스물한자이상입니다초과입니다")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("닉네임은 최대 20자까지 입력 가능합니다."));
    }

    // ===== sendVerificationEmail =====

    @DisplayName("이메일 인증 메일을 전송한다.")
    @Test
    @WithMockUser(roles = "USER")
    void sendVerificationEmail() throws Exception {
        // given
        EmailSendRequest request = EmailSendRequest.builder()
                .email("test@example.com")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("이메일 인증 메일 전송 시 이메일은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void sendVerificationEmailWithoutEmail() throws Exception {
        // given
        EmailSendRequest request = EmailSendRequest.builder()
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("이메일은 필수입니다."));
    }

    @DisplayName("이메일 인증 메일 전송 시 이메일 형식이 올바르지 않으면 안된다.")
    @Test
    @WithMockUser(roles = "USER")
    void sendVerificationEmailWithInvalidEmail() throws Exception {
        // given
        EmailSendRequest request = EmailSendRequest.builder()
                .email("not-an-email")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/user/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다."));
    }

    // ===== getMyProfile =====

    @DisplayName("내 프로필을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void getMyProfile() throws Exception {
        // when // then
        mockMvc.perform(
                        get("/api/v1/user/profile")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    // ===== updateProfile =====

    @DisplayName("프로필을 수정한다.")
    @Test
    @WithMockUser(roles = "USER")
    void updateProfile() throws Exception {
        // given
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .nickName("새닉네임")
                .profileUrl("https://example.com/profile.jpg")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/v1/user/profile")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("프로필 수정 시 닉네임은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void updateProfileWithoutNickName() throws Exception {
        // given
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .profileUrl("https://example.com/profile.jpg")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/v1/user/profile")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("닉네임은 필수입니다."));
    }
}
