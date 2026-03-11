package com.puppynoteserver.community.post.service;

import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;

public interface CommunityPostReadService {

    PostListResponse getPosts(String keyword, int page, int size);

    PostResponse getPost(Long postId);
}
