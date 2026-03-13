package com.puppynoteserver.community.post.event;

import com.puppynoteserver.community.post.entity.Post;

import java.util.List;

public record PostIndexEvent(Post post, List<String> hashtags) {
}
