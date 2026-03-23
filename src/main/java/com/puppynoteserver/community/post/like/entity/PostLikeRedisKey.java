package com.puppynoteserver.community.post.like.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostLikeRedisKey {

    USER_LIKED("user:liked:"),               // user:liked:{userId}:{postId} → "1"/"0"
    POST_LIKE_USERS("post:like:users:"),      // post:like:users:{postId} → Set<userId>
    DIRTY("post:like:dirty"),                 // post:like:dirty → Set<postId>
    DIRTY_PROCESSING("post:like:dirty:processing:"); // post:like:dirty:processing:{ts}

    private final String key;

    public String of(Object... args) {
        StringBuilder sb = new StringBuilder(key);
        for (Object arg : args) {
            sb.append(arg).append(":");
        }
        // 마지막 구분자 제거
        if (args.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
