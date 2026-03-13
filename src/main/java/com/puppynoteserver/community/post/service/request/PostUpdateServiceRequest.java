package com.puppynoteserver.community.post.service.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateServiceRequest {

    private final String content;
    private final List<String> hashtags;
    private final List<String> imageKeys;

    @Builder
    private PostUpdateServiceRequest(String content, List<String> hashtags, List<String> imageKeys) {
        this.content = content;
        this.hashtags = hashtags;
        this.imageKeys = imageKeys;
    }
}
