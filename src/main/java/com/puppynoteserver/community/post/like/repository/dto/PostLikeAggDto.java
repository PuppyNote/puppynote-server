package com.puppynoteserver.community.post.like.repository.dto;

import lombok.Getter;

@Getter
public class PostLikeAggDto {

    private final Long postId;
    private final Long likeCount;
    private final Long isLikedCount;

    public PostLikeAggDto(Long postId, Long likeCount, Long isLikedCount) {
        this.postId = postId;
        this.likeCount = likeCount != null ? likeCount : 0L;
        this.isLikedCount = isLikedCount != null ? isLikedCount : 0L;
    }
}
