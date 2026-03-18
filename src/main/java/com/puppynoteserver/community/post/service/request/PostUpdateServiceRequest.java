package com.puppynoteserver.community.post.service.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateServiceRequest {

    private final String content;
    private final List<String> hashtags;
    private final List<String> addImageKeys;
    private final List<String> deleteImageKeys;

    @Builder
    private PostUpdateServiceRequest(String content, List<String> hashtags,
                                     List<String> addImageKeys, List<String> deleteImageKeys) {
        this.content = content;
        this.hashtags = hashtags;
        this.addImageKeys = addImageKeys;
        this.deleteImageKeys = deleteImageKeys;
    }
}
