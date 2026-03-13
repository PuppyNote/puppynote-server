package com.puppynoteserver.community.post.controller;

import com.puppynoteserver.community.post.controller.request.PostCreateRequest;
import com.puppynoteserver.community.post.controller.request.PostUpdateRequest;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.CommunityPostWriteService;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;
import com.puppynoteserver.global.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class CommunityPostController {

    private final CommunityPostReadService communityPostReadService;
    private final CommunityPostWriteService communityPostWriteService;

    // 게시물 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<Long> createPost(@RequestBody @Valid PostCreateRequest request) {
        return ApiResponse.created(communityPostWriteService.createPost(request.toServiceRequest()));
    }

    // 게시물 목록 조회 (페이징 + 검색)
    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(communityPostReadService.getPosts(keyword, page, size));
    }

    // 게시물 단건 조회
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        return ApiResponse.ok(communityPostReadService.getPost(postId));
    }

    // 게시물 수정
    @PatchMapping("/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request) {
        communityPostWriteService.updatePost(postId, request.toServiceRequest());
        return ApiResponse.ok(null);
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        communityPostWriteService.deletePost(postId);
        return ApiResponse.ok(null);
    }

    // 해시태그 자동완성
    @GetMapping("/hashtags")
    public ApiResponse<List<String>> getHashtagSuggestions(@RequestParam @NotBlank String keyword) {
        return ApiResponse.ok(communityPostReadService.getHashtagSuggestions(keyword));
    }
}
