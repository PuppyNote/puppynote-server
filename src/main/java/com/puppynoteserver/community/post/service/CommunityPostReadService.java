package com.puppynoteserver.community.post.service;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;

import java.util.List;

public interface CommunityPostReadService {

    PostListResponse getPosts(String keyword, int page, int size);

    PostListResponse getMyPosts(int page, int size);

    PostResponse getPost(Long postId);

    List<String> getHashtagSuggestions(String keyword);

    // 게시물 존재 여부 확인 (삭제된 게시물 포함 시 NotFoundException)
    Post getPostOrThrow(Long postId);
}
