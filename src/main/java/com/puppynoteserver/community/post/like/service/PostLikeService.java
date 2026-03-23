package com.puppynoteserver.community.post.like.service;

import com.puppynoteserver.community.post.like.service.response.PostLikeToggleResponse;

public interface PostLikeService {

    // 좋아요 토글 (좋아요/취소)
    PostLikeToggleResponse toggleLike(Long postId);
}
