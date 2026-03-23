package com.puppynoteserver.community.post.like.service.impl;

import com.puppynoteserver.community.post.like.repository.PostLikeRepository;
import com.puppynoteserver.community.post.like.service.PostLikeService;
import com.puppynoteserver.community.post.like.service.response.PostLikeToggleResponse;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.redis.PostLikeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final CommunityPostReadService communityPostReadService;
    private final PostLikeRedisService postLikeRedisService;
    private final SecurityService securityService;

    @Override
    @Transactional
    public PostLikeToggleResponse toggleLike(Long postId) {
        communityPostReadService.getPostOrThrow(postId);

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();

        initializeCacheIfAbsent(postId, userId);

        List<Long> result = postLikeRedisService.toggle(userId, postId);
        boolean liked = result.get(0) == 1L;
        long likeCount = result.get(1);
        return PostLikeToggleResponse.of(liked, likeCount);
    }

    // 캐시 미스 시 DB에서 좋아요 유저 목록 전체를 Redis에 로드
    private void initializeCacheIfAbsent(Long postId, Long userId) {
        if (postLikeRedisService.existsUsersCache(postId)) {
            // users Set은 있지만 개인 캐시가 없는 경우 Set에서 판단
            if (!postLikeRedisService.existsLikedCache(userId, postId)) {
                boolean liked = postLikeRedisService.isLiked(postId, userId);
                postLikeRedisService.setLikedCache(userId, postId, liked);
            }
            return;
        }

        // users Set 자체가 없으면 DB에서 전체 로드
        Set<Long> dbUserIds = postLikeRepository.findUserIdsByPostId(postId);
        postLikeRedisService.initializeUsersCache(postId, dbUserIds, userId);
    }
}
