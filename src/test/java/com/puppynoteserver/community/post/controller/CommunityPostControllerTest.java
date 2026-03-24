package com.puppynoteserver.community.post.controller;

import com.puppynoteserver.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommunityPostControllerTest extends ControllerTestSupport {

    @DisplayName("게시물을 등록한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물을_등록한다() throws Exception {
        // given
        Map<String, Object> request = new HashMap<>();
        request.put("content", "테스트 내용입니다.");
        request.put("hashtags", List.of("강아지", "산책"));
        request.put("imageKeys", List.of());

        // when // then
        mockMvc.perform(
                post("/api/v1/community/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"));
    }

    @DisplayName("게시물 등록 시 내용은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물_등록_시_내용은_필수이다() throws Exception {
        // given
        Map<String, Object> request = new HashMap<>();
        request.put("hashtags", List.of("강아지"));
        request.put("imageKeys", List.of());

        // when // then
        mockMvc.perform(
                post("/api/v1/community/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("내용은 필수입니다."));
    }

    @DisplayName("게시물 목록을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물_목록을_조회한다() throws Exception {
        // when // then
        mockMvc.perform(
                get("/api/v1/community/posts")
                        .param("page", "0")
                        .param("size", "20")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("내가 작성한 게시물 목록을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 내가_작성한_게시물_목록을_조회한다() throws Exception {
        // when // then
        mockMvc.perform(
                get("/api/v1/community/posts/my")
                        .param("page", "0")
                        .param("size", "20")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("게시물 단건을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물_단건을_조회한다() throws Exception {
        // when // then
        mockMvc.perform(
                get("/api/v1/community/posts/{postId}", 1L)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("게시물을 수정한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물을_수정한다() throws Exception {
        // given
        Map<String, Object> request = new HashMap<>();
        request.put("content", "수정된 내용입니다.");
        request.put("hashtags", List.of("강아지"));
        request.put("addImageKeys", List.of());
        request.put("deleteImageKeys", List.of());

        // when // then
        mockMvc.perform(
                patch("/api/v1/community/posts/{postId}", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("게시물 수정 시 내용은 필수이다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물_수정_시_내용은_필수이다() throws Exception {
        // given
        Map<String, Object> request = new HashMap<>();
        request.put("hashtags", List.of("강아지"));
        request.put("addImageKeys", List.of());
        request.put("deleteImageKeys", List.of());

        // when // then
        mockMvc.perform(
                patch("/api/v1/community/posts/{postId}", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("내용은 필수입니다."));
    }

    @DisplayName("게시물을 삭제한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 게시물을_삭제한다() throws Exception {
        // when // then
        mockMvc.perform(
                delete("/api/v1/community/posts/{postId}", 1L)
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @DisplayName("해시태그 자동완성 목록을 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    void 해시태그_자동완성_목록을_조회한다() throws Exception {
        // when // then
        mockMvc.perform(
                get("/api/v1/community/posts/hashtags")
                        .param("keyword", "강아지")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }
}
