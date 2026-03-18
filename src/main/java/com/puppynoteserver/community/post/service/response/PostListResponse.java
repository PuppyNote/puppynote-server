package com.puppynoteserver.community.post.service.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponse {

    private final List<PostResponse> posts;
    private final int currentPage;
    private final int totalPages;
    private final long totalCount;

    private PostListResponse(List<PostResponse> posts, int currentPage, int totalPages, long totalCount) {
        this.posts = posts;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }

    public static PostListResponse of(List<PostResponse> posts, int currentPage, int totalPages, long totalCount) {
        return new PostListResponse(posts, currentPage, totalPages, totalCount);
    }
}
