package com.puppynoteserver.batch;

import com.puppynoteserver.community.like.entity.PostLikeRedisKey;
import com.puppynoteserver.community.like.repository.PostLikeRepository;
import com.puppynoteserver.redis.service.PostLikeRedisService;
import com.puppynoteserver.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeSyncBatch {

    private final PostLikeRepository postLikeRepository;
    private final PostLikeRedisService postLikeRedisService;
    private final RedisService redisService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncLikesToDB() {
        if (!redisService.hasKey(PostLikeRedisKey.DIRTY.of())) {
            return;
        }

        // RENAME으로 dirty set을 원자적으로 분리 (처리 중 새 항목은 원본 키에 계속 쌓임)
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
        long timestamp = System.currentTimeMillis();

        // delta set을 원자적으로 분리 (처리 중 새 변경은 원본 키에 계속 쌓임)
        Set<String> deltaAdd = postLikeRedisService.popDeltaAdd(postId, timestamp);
        Set<String> deltaRemove = postLikeRedisService.popDeltaRemove(postId, timestamp);

        if (deltaAdd.isEmpty() && deltaRemove.isEmpty()) {
            return;
        }

        List<Long> toInsert = deltaAdd.stream().map(Long::parseLong).toList();
        List<Long> toDelete = deltaRemove.stream().map(Long::parseLong).toList();

        // INSERT IGNORE: like→unlike→like 같이 동일 주기 내 중복 시도 무시
        toInsert.forEach(userId -> postLikeRepository.insertIgnore(postId, userId));

        if (!toDelete.isEmpty()) {
            postLikeRepository.deleteByPostIdAndUserIdIn(postId, toDelete);
        }

        log.info("[좋아요 배치 동기화] postId={} insert={} delete={}", postId, toInsert.size(), toDelete.size());
    }
}
