package com.puppynoteserver.batch;

import com.puppynoteserver.community.post.like.entity.PostLike;
import com.puppynoteserver.community.post.like.entity.PostLikeRedisKey;
import com.puppynoteserver.community.post.like.repository.PostLikeRepository;
import com.puppynoteserver.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeSyncBatch {

    private final PostLikeRepository postLikeRepository;
    private final RedisService redisService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncLikesToDB() {
        // RENAME으로 dirty set을 원자적으로 분리 (처리 중 새 항목은 원본 키에 계속 쌓임)
        if (!redisService.hasKey(PostLikeRedisKey.DIRTY.of())) {
            return;
        }
        String processingKey = PostLikeRedisKey.DIRTY_PROCESSING.of(System.currentTimeMillis());
        redisService.rename(PostLikeRedisKey.DIRTY.of(), processingKey);

        Set<String> dirtyPostIds = redisService.sMembers(processingKey);
        redisService.delete(processingKey);

        if (dirtyPostIds == null || dirtyPostIds.isEmpty()) {
            return;
        }

        log.info("[좋아요 배치 동기화] 처리 대상 게시물 수: {}", dirtyPostIds.size());

        for (String postIdStr : dirtyPostIds) {
            try {
                syncPost(Long.parseLong(postIdStr));
            } catch (Exception e) {
                log.error("[좋아요 배치 동기화] 게시물 동기화 실패 postId={}", postIdStr, e);
                // 실패한 postId는 dirty set에 다시 추가해 다음 사이클에서 재시도
                redisService.sAdd(PostLikeRedisKey.DIRTY.of(), postIdStr);
            }
        }
    }

    @Transactional
    public void syncPost(Long postId) {
        String usersKey = PostLikeRedisKey.POST_LIKE_USERS.of(postId);

        // Set이 만료된 경우 스킵 (마지막 sync 이후 변경 없음)
        if (!redisService.hasKey(usersKey)) {
            log.debug("[좋아요 배치 동기화] Redis Set 만료로 스킵 postId={}", postId);
            return;
        }

        Set<String> redisMembers = redisService.sMembers(usersKey);
        Set<Long> redisUserIds = redisMembers == null
                ? Set.of()
                : redisMembers.stream().map(Long::parseLong).collect(Collectors.toSet());

        Set<Long> dbUserIds = postLikeRepository.findUserIdsByPostId(postId);

        // 새로 좋아요한 유저 INSERT
        List<PostLike> toInsert = redisUserIds.stream()
                .filter(uid -> !dbUserIds.contains(uid))
                .map(uid -> PostLike.of(postId, uid))
                .toList();

        // 좋아요 취소한 유저 DELETE
        List<Long> toDelete = dbUserIds.stream()
                .filter(uid -> !redisUserIds.contains(uid))
                .toList();

        if (!toInsert.isEmpty()) {
            postLikeRepository.saveAll(toInsert);
        }
        if (!toDelete.isEmpty()) {
            postLikeRepository.deleteByPostIdAndUserIdIn(postId, toDelete);
        }

        log.info("[좋아요 배치 동기화] postId={} insert={} delete={}", postId, toInsert.size(), toDelete.size());
    }
}
