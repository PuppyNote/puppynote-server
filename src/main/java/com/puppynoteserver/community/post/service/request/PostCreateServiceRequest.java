package com.puppynoteserver.community.post.service.request;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.user.users.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostCreateServiceRequest {

    private final String content;
    private final List<String> hashtags;
    private final List<String> imageKeys;

    @Builder
    private PostCreateServiceRequest(String content, List<String> hashtags, List<String> imageKeys) {
        this.content = content;
        this.hashtags = hashtags;
        this.imageKeys = imageKeys;
    }

    public Post toEntity(User user) {
        return Post.of(user, content, hashtags);
    }
}
