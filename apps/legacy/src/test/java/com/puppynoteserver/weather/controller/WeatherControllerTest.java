package com.puppynoteserver.weather.controller;

import com.puppynoteserver.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WeatherControllerTest extends ControllerTestSupport {

    @DisplayName("날씨를 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 날씨를_조회한다() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/weather")
                        .param("latitude", "37.56")
                        .param("longitude", "126.97"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("날씨 조회 시 위도는 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 날씨_조회_시_위도는_필수이다() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/weather")
                        .param("longitude", "126.97"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("위도는 필수입니다."));
    }

    @DisplayName("날씨 조회 시 경도는 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 날씨_조회_시_경도는_필수이다() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/weather")
                        .param("latitude", "37.56"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.message").value("경도는 필수입니다."));
    }
}
