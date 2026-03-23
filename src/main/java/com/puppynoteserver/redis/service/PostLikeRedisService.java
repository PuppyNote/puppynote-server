package com.puppynoteserver.redis.service;

import com.puppynoteserver.community.like.entity.PostLikeRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostLikeRedisService {

    private final RedisService redisService;

    static final Duration USER_LIKED_TTL = Duration.ofDays(7);
    static final Duration POST_COUNT_TTL = Duration.ofDays(30);

    // 원자적 토글 스크립트
    // KEYS[1] = user:liked:{userId}:{postId}
    // KEYS[2] = post:like:count:{postId}
    // KEYS[3] = post:like:dirty
    // KEYS[4] = post:like:delta:add:{postId}
    // KEYS[5] = post:like:delta:remove:{postId}
    // ARGV[1] = userId, ARGV[2] = postId, ARGV[3] = likedTtl(초), ARGV[4] = countTtl(초)
    // return [liked(1=좋아요/0=취소), likeCount]
    @SuppressWarnings("unchecked")
    private static final DefaultRedisScript<List<Long>> TOGGLE_SCRIPT;

    static {
        TOGGLE_SCRIPT = new DefaultRedisScript<>();
        TOGGLE_SCRIPT.setScriptText(
                "local likedKey    = KEYS[1]\n" +
                "local countKey    = KEYS[2]\n" +
                "local dirtyKey    = KEYS[3]\n" +
                "local deltaAddKey = KEYS[4]\n" +
                "local deltaRemKey = KEYS[5]\n" +
                "local userId   = ARGV[1]\n" +
                "local postId   = ARGV[2]\n" +
                "local likedTtl = tonumber(ARGV[3])\n" +
                "local countTtl = tonumber(ARGV[4])\n" +
                "local current = redis.call('GET', likedKey)\n" +
                "local liked\n" +
                "local likeCount\n" +
                "if current == '1' then\n" +
                "    redis.call('SET', likedKey, '0', 'EX', likedTtl)\n" +
                "    likeCount = redis.call('DECR', countKey)\n" +
                "    redis.call('SADD', deltaRemKey, userId)\n" +
                "    redis.call('SREM', deltaAddKey, userId)\n" +
                "    liked = 0\n" +
                "else\n" +
                "    redis.call('SET', likedKey, '1', 'EX', likedTtl)\n" +
                "    likeCount = redis.call('INCR', countKey)\n" +
                "    redis.call('SADD', deltaAddKey, userId)\n" +
                "    redis.call('SREM', deltaRemKey, userId)\n" +
                "    liked = 1\n" +
                "end\n" +
                "redis.call('EXPIRE', countKey, countTtl)\n" +
                "redis.call('SADD', dirtyKey, postId)\n" +
                "return {liked, likeCount}"
        );
        TOGGLE_SCRIPT.setResultType((Class<List<Long>>) (Class<?>) List.class);
    }

    // 유저의 개인 좋아요 상태 캐시가 존재하는지 확인한다
    public boolean existsLikedCache(Long userId, Long postId) {
        return redisService.hasKey(PostLikeRedisKey.USER_LIKED.of(userId, postId));
    }

    // 게시물의 좋아요 수 캐시가 존재하는지 확인한다
    public boolean existsCountCache(Long postId) {
        return redisService.hasKey(PostLikeRedisKey.POST_LIKE_COUNT.of(postId));
    }

    // 유저의 개인 좋아요 상태 캐시를 저장한다
    public void setLikedCache(Long userId, Long postId, boolean liked) {
        redisService.setValue(
                PostLikeRedisKey.USER_LIKED.of(userId, postId),
                liked ? "1" : "0",
                USER_LIKED_TTL
        );
    }

    // 게시물의 좋아요 수 캐시를 저장한다
    public void setCountCache(Long postId, long count) {
        redisService.setValue(
                PostLikeRedisKey.POST_LIKE_COUNT.of(postId),
                String.valueOf(count),
                POST_COUNT_TTL
        );
    }

    // 여러 게시물의 liked 상태를 한 번에 조회한다 (MGET)
    // 캐시 미스인 postId는 Map에 포함되지 않아 DB fallback이 필요하다
    public Map<Long, Boolean> getLikedStatusBatch(List<Long> postIds, Long userId) {
        List<String> keys = postIds.stream()
                .map(postId -> PostLikeRedisKey.USER_LIKED.of(userId, postId))
                .toList();

        List<String> values = redisService.mGet(keys);

        Map<Long, Boolean> result = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(postIds.get(i), "1".equals(value));
            }
        }
        return result;
    }

    // 여러 게시물의 likeCount를 한 번에 조회한다 (MGET)
    // 캐시 미스인 postId는 Map에 포함되지 않아 DB fallback이 필요하다
    public Map<Long, Long> getLikeCountBatch(List<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(postId -> PostLikeRedisKey.POST_LIKE_COUNT.of(postId))
                .toList();

        List<String> values = redisService.mGet(keys);

        Map<Long, Long> result = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(postIds.get(i), Long.parseLong(value));
            }
        }
        return result;
    }

    // 단건 게시물의 liked 상태를 조회한다
    // 캐시 미스이면 Optional.empty()를 반환해 DB fallback이 필요하다
    public Optional<Boolean> getLikedStatus(Long postId, Long userId) {
        String value = redisService.getValue(PostLikeRedisKey.USER_LIKED.of(userId, postId));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of("1".equals(value));
    }

    // 단건 게시물의 likeCount를 조회한다
    // 캐시 미스이면 Optional.empty()를 반환해 DB fallback이 필요하다
    public Optional<Long> getLikeCount(Long postId) {
        String value = redisService.getValue(PostLikeRedisKey.POST_LIKE_COUNT.of(postId));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(value));
    }

    // delta:add Set을 원자적으로 분리해 이번 사이클에 새로 좋아요한 userId 목록을 반환한다
    // 키가 없으면 빈 Set을 반환한다
    public Set<String> popDeltaAdd(Long postId, long timestamp) {
        return popDelta(PostLikeRedisKey.DELTA_ADD.of(postId),
                PostLikeRedisKey.DELTA_ADD_PROCESSING.of(postId, timestamp));
    }

    // delta:remove Set을 원자적으로 분리해 이번 사이클에 좋아요 취소한 userId 목록을 반환한다
    // 키가 없으면 빈 Set을 반환한다
    public Set<String> popDeltaRemove(Long postId, long timestamp) {
        return popDelta(PostLikeRedisKey.DELTA_REMOVE.of(postId),
                PostLikeRedisKey.DELTA_REMOVE_PROCESSING.of(postId, timestamp));
    }

    // 좋아요 상태를 원자적으로 토글하고 [liked(1/0), likeCount]를 반환한다
    public List<Long> toggle(Long userId, Long postId) {
        return redisService.execute(
                TOGGLE_SCRIPT,
                List.of(
                        PostLikeRedisKey.USER_LIKED.of(userId, postId),
                        PostLikeRedisKey.POST_LIKE_COUNT.of(postId),
                        PostLikeRedisKey.DIRTY.of(),
                        PostLikeRedisKey.DELTA_ADD.of(postId),
                        PostLikeRedisKey.DELTA_REMOVE.of(postId)
                ),
                String.valueOf(userId),
                String.valueOf(postId),
                String.valueOf(USER_LIKED_TTL.getSeconds()),
                String.valueOf(POST_COUNT_TTL.getSeconds())
        );
    }

    private Set<String> popDelta(String deltaKey, String processingKey) {
        if (!redisService.hasKey(deltaKey)) {
            return Set.of();
        }
        redisService.rename(deltaKey, processingKey);
        Set<String> members = redisService.sMembers(processingKey);
        redisService.delete(processingKey);
        return members == null ? Set.of() : members;
    }
}
