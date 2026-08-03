package com.puppynoteserver.user.push.controller;

import com.puppynoteserver.ControllerTestSupport;
import com.puppynoteserver.user.push.controller.request.DeviceTokenRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeviceTokenControllerTest extends ControllerTestSupport {

    @DisplayName("디바이스 토큰을 등록한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 디바이스_토큰을_등록한다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .deviceId("device-1234")
                .pushToken("ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]")
                .platform("IOS")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("웹 플랫폼으로도 디바이스 토큰을 등록할 수 있다.")
    @Test
    @WithMockUser(roles = "USER")
    void 웹_플랫폼으로도_디바이스_토큰을_등록할_수_있다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .deviceId("device-web-1234")
                .pushToken("web-push-token")
                .platform("WEB")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("플랫폼 값은 대소문자를 구분하지 않는다.")
    @Test
    @WithMockUser(roles = "USER")
    void 플랫폼_값은_대소문자를_구분하지_않는다() throws Exception {
        // given
        String requestBody = """
                {
                  "deviceId": "device-1234",
                  "pushToken": "web-push-token",
                  "platform": "web"
                }
                """;

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("디바이스 토큰 등록 시 디바이스ID는 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 디바이스_토큰_등록_시_디바이스ID는_필수이다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .pushToken("ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]")
                .platform("ANDROID")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("디바이스ID는 필수입니다."));
    }

    @DisplayName("디바이스 토큰 등록 시 디바이스 토큰은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 디바이스_토큰_등록_시_디바이스_토큰은_필수이다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .deviceId("device-1234")
                .platform("ANDROID")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("디바이스 토큰은 필수입니다."));
    }

    @DisplayName("디바이스 토큰 등록 시 플랫폼은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 디바이스_토큰_등록_시_플랫폼은_필수이다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .deviceId("device-1234")
                .pushToken("ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("플랫폼은 필수입니다."));
    }

    @DisplayName("지원하지 않는 플랫폼 값으로 디바이스 토큰을 등록하면 400을 응답한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 지원하지_않는_플랫폼_값으로_디바이스_토큰을_등록하면_400을_응답한다() throws Exception {
        // given
        DeviceTokenRegisterRequest request = DeviceTokenRegisterRequest.builder()
                .deviceId("device-1234")
                .pushToken("ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]")
                .platform("WINDOWS")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("지원하지 않는 플랫폼입니다. (IOS, ANDROID, WEB)"));
    }
}
