package com.puppynoteserver.community.like.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostLikeRedisKey {

    USER_LIKED("user:liked:"),                          // user:liked:{userId}:{postId} → "1"/"0"
    POST_LIKE_COUNT("post:like:count:"),                // post:like:count:{postId} → 총 좋아요 수
    DIRTY("post:like:dirty"),                           // post:like:dirty → Set<postId>
    DIRTY_PROCESSING("post:like:dirty:processing:"),    // post:like:dirty:processing:{ts}
    DELTA_ADD("post:like:delta:add:"),                  // post:like:delta:add:{postId} → 새로 좋아요한 userId Set
    DELTA_REMOVE("post:like:delta:remove:"),            // post:like:delta:remove:{postId} → 좋아요 취소한 userId Set
    DELTA_ADD_PROCESSING("post:like:delta:add:processing:"),       // 배치 처리 중 임시 키
    DELTA_REMOVE_PROCESSING("post:like:delta:remove:processing:"); // 배치 처리 중 임시 키

    private final String key;

    public String of(Object... args) {
        StringBuilder sb = new StringBuilder(key);
        for (Object arg : args) {
            sb.append(arg).append(":");
        }
        if (args.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
