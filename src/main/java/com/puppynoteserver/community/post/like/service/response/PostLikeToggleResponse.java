package com.puppynoteserver.community.post.like.service.response;

import lombok.Getter;

@Getter
public class PostLikeToggleResponse {

    private final boolean liked;
    private final long likeCount;

    private PostLikeToggleResponse(boolean liked, long likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public static PostLikeToggleResponse of(boolean liked, long likeCount) {
        return new PostLikeToggleResponse(liked, likeCount);
    }
}
