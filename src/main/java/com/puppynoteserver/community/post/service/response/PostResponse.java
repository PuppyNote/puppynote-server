package com.puppynoteserver.community.post.service.response;

import com.puppynoteserver.community.post.entity.Post;
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
    private final List<String> imageKeys;
    private final List<String> imageUrls;
    private final List<String> hashtags;
    private final LocalDateTime createdDate;
    private final long likeCount;
    private final boolean isLiked;

    @Builder
    private PostResponse(Long postId, Long userId, String userNickname, String userProfileUrl,
                         String content, List<String> imageKeys, List<String> imageUrls,
                         List<String> hashtags, LocalDateTime createdDate,
                         long likeCount, boolean isLiked) {
        this.postId = postId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfileUrl = userProfileUrl;
        this.content = content;
        this.imageKeys = imageKeys;
        this.imageUrls = imageUrls;
        this.hashtags = hashtags;
        this.createdDate = createdDate;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    public static PostResponse of(Post post, String userProfileUrl,
                                  List<String> imageKeys, List<String> imageUrls,
                                  long likeCount, boolean isLiked) {
        return PostResponse.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .userNickname(post.getUser().getNickName())
                .userProfileUrl(userProfileUrl)
                .content(post.getContent())
                .imageKeys(imageKeys)
                .imageUrls(imageUrls)
                .hashtags(List.copyOf(post.getHashtags()))
                .createdDate(post.getCreatedDate())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}
