package com.puppynoteserver.community.post.like.service.impl;

import com.puppynoteserver.community.post.like.entity.PostLike;
import com.puppynoteserver.community.post.like.repository.PostLikeRepository;
import com.puppynoteserver.community.post.like.service.PostLikeService;
import com.puppynoteserver.community.post.like.service.response.PostLikeToggleResponse;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.global.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final CommunityPostReadService communityPostReadService;
    private final StringRedisTemplate redisTemplate;
    private final SecurityService securityService;

    // Redis는 토글 원자성 보장에만 사용
    // user:liked:{userId}:{postId} → "1"(좋아요) / "0"(안함)
    private static final String USER_LIKED_KEY_PREFIX = "user:liked:";
    private static final Duration CACHE_TTL = Duration.ofDays(7);

    // Redis Lua 스크립트: 원자적 토글
    // KEYS[1] = user:liked:{userId}:{postId}
    // ARGV[1] = ttl (초)
    // return 1 = 좋아요, 0 = 취소
    private static final DefaultRedisScript<Long> TOGGLE_SCRIPT;

    static {
        TOGGLE_SCRIPT = new DefaultRedisScript<>();
        TOGGLE_SCRIPT.setScriptText(
                "local key = KEYS[1]\n" +
                        "local ttl = tonumber(ARGV[1])\n" +
                        "local current = redis.call('GET', key)\n" +
                        "if current == '1' then\n" +
                        "    redis.call('SET', key, '0', 'EX', ttl)\n" +
                        "    return 0\n" +
                        "else\n" +
                        "    redis.call('SET', key, '1', 'EX', ttl)\n" +
                        "    return 1\n" +
                        "end"
        );
        TOGGLE_SCRIPT.setResultType(Long.class);
    }

    @Override
    public PostLikeToggleResponse toggleLike(Long postId) {
        communityPostReadService.getPostOrThrow(postId);

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        String isLikedKey = createUserLikedKey(userId, postId);

        // 캐시 미스 시에만 DB에서 현재 좋아요 상태 로드
        if (!redisTemplate.hasKey(isLikedKey)) {
            boolean liked = postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
            redisTemplate.opsForValue().set(isLikedKey, liked ? "1" : "0", CACHE_TTL);
        }

        // Lua 스크립트로 원자적 토글
        Long result = redisTemplate.execute(TOGGLE_SCRIPT, List.of(isLikedKey),
                String.valueOf(CACHE_TTL.getSeconds()));
        boolean liked = result == 1L;

        // DB 동기화
        if (liked) {
            if (postLikeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
                postLikeRepository.save(PostLike.of(postId, userId));
            }
        } else {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        }

        // 좋아요 수는 DB에서 직접 조회
        long likeCount = postLikeRepository.countByPostId(postId);
        return PostLikeToggleResponse.of(liked, likeCount);
    }

    private String createUserLikedKey(Long userId, Long postId) {
        return USER_LIKED_KEY_PREFIX + userId + ":" + postId;
    }
}
