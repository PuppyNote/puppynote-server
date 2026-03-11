package com.puppynoteserver.community.post.service;

import com.puppynoteserver.community.post.service.request.PostCreateServiceRequest;
import com.puppynoteserver.community.post.service.request.PostUpdateServiceRequest;

public interface CommunityPostWriteService {

    Long createPost(PostCreateServiceRequest request);

    void updatePost(Long postId, PostUpdateServiceRequest request);
}
