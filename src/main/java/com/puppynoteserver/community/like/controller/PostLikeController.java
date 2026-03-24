package com.puppynoteserver.community.like.controller;

import com.puppynoteserver.community.like.service.PostLikeService;
import com.puppynoteserver.community.like.service.response.PostLikeToggleResponse;
import com.puppynoteserver.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 게시물 좋아요 토글 (좋아요/좋아요 취소)
    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeToggleResponse> toggleLike(@PathVariable Long postId) {
        return ApiResponse.ok(postLikeService.toggleLike(postId));
    }
}
