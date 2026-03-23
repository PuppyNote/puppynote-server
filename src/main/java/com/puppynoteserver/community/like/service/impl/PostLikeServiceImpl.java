package com.puppynoteserver.community.like.service.impl;

import com.puppynoteserver.community.like.repository.PostLikeRepository;
import com.puppynoteserver.community.like.service.PostLikeService;
import com.puppynoteserver.community.like.service.response.PostLikeToggleResponse;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.redis.PostLikeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 캐시 미스 시 DB에서 각각 조회해 초기화
    // count: countByPostId 1번 / liked 여부: findByPostIdAndUserId 1번
    private void initializeCacheIfAbsent(Long postId, Long userId) {
        if (!postLikeRedisService.existsCountCache(postId)) {
            long count = postLikeRepository.countByPostId(postId);
            postLikeRedisService.setCountCache(postId, count);
        }

        if (!postLikeRedisService.existsLikedCache(userId, postId)) {
            boolean liked = postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
            postLikeRedisService.setLikedCache(userId, postId, liked);
        }
    }
}
