package com.puppynoteserver.community.post.event;

import com.puppynoteserver.community.post.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class PostEventListener {

    private final PostSearchService postSearchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostIndex(PostIndexEvent event) {
        postSearchService.indexPost(event.post(), event.hashtags());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostDelete(PostDeleteEvent event) {
        postSearchService.deletePost(event.postId());
    }
}
