package com.puppynoteserver.pet.pets.controller;

import com.puppynoteserver.ControllerTestSupport;
import com.puppynoteserver.pet.pets.controller.request.PetCreateRequest;
import com.puppynoteserver.pet.pets.controller.request.PetUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PetControllerTest extends ControllerTestSupport {

    @DisplayName("내 펫 목록을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 내_펫_목록을_조회한다() throws Exception {
        // when // then
        mockMvc.perform(
                get("/api/v1/pets")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("펫을 생성한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 펫을_생성한다() throws Exception {
        // given
        PetCreateRequest request = PetCreateRequest.builder()
            .name("멍멍이")
            .build();

        // when // then
        mockMvc.perform(
                post("/api/v1/pets")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value("201"));
    }

    @DisplayName("펫을 생성할 시 펫 이름은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 펫을_생성할_시_펫_이름은_필수이다() throws Exception {
        // given
        PetCreateRequest request = PetCreateRequest.builder()
            .build();

        // when // then
        mockMvc.perform(
                post("/api/v1/pets")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value("400"))
            .andExpect(jsonPath("$.message").value("펫 이름은 필수입니다."));
    }

    @DisplayName("펫 정보를 수정한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 펫_정보를_수정한다() throws Exception {
        // given
        PetUpdateRequest request = PetUpdateRequest.builder()
            .name("멍멍이수정")
            .build();

        // when // then
        mockMvc.perform(
                patch("/api/v1/pets/{petId}", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("펫 정보를 수정할 시 펫 이름은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 펫_정보를_수정할_시_펫_이름은_필수이다() throws Exception {
        // given
        PetUpdateRequest request = PetUpdateRequest.builder()
            .build();

        // when // then
        mockMvc.perform(
                patch("/api/v1/pets/{petId}", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value("400"))
            .andExpect(jsonPath("$.message").value("펫 이름은 필수입니다."));
    }

    @DisplayName("펫을 삭제한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 펫을_삭제한다() throws Exception {
        // when // then
        mockMvc.perform(
                delete("/api/v1/pets/{petId}", 1L)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value("200"));
    }
}
