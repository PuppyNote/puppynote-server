package com.puppynoteserver.community.post.service.response;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.global.util.HashtagExtractor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponse {

    private final Long postId;
    private final Long userId;
    private final String userNickname;
    private final String userProfileUrl;
    private final String content;
    private final List<String> imageUrls;
    private final List<String> hashtags;
    private final LocalDateTime createdDate;

    @Builder
    private PostResponse(Long postId, Long userId, String userNickname, String userProfileUrl,
                         String content, List<String> imageUrls, List<String> hashtags,
                         LocalDateTime createdDate) {
        this.postId = postId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfileUrl = userProfileUrl;
        this.content = content;
        this.imageUrls = imageUrls;
        this.hashtags = hashtags;
        this.createdDate = createdDate;
    }

    public static PostResponse of(Post post, String userProfileUrl,
                                  List<String> imageUrls, HashtagExtractor hashtagExtractor) {
        return PostResponse.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .userNickname(post.getUser().getNickName())
                .userProfileUrl(userProfileUrl)
                .content(post.getContent())
                .imageUrls(imageUrls)
                .hashtags(hashtagExtractor.extract(post.getContent()))
                .createdDate(post.getCreatedDate())
                .build();
    }
}
