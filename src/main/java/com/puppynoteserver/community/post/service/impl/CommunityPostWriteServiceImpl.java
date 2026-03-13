package com.puppynoteserver.community.post.service.impl;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.entity.PostImage;
import com.puppynoteserver.community.post.event.PostDeleteEvent;
import com.puppynoteserver.community.post.event.PostIndexEvent;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.community.post.service.CommunityPostWriteService;
import com.puppynoteserver.community.post.service.request.PostCreateServiceRequest;
import com.puppynoteserver.community.post.service.request.PostUpdateServiceRequest;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.UnauthenticatedException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPostWriteServiceImpl implements CommunityPostWriteService {

    private final PostRepository postRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long createPost(PostCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Post post = request.toEntity(user);
        postRepository.save(post);

        // 이미지 키 저장
        addImages(post, request.getImageKeys());

        // ES 인덱싱 이벤트 발행 (트랜잭션 커밋 후 비동기 실행)
        eventPublisher.publishEvent(new PostIndexEvent(post, request.getHashtags()));

        return post.getId();
    }

    @Override
    public void updatePost(Long postId, PostUpdateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthenticatedException("게시물 수정 권한이 없습니다.");
        }

        post.updateContent(request.getContent());
        post.updateHashtags(request.getHashtags());

        // 삭제할 이미지 제거
        if (request.getDeleteImageKeys() != null && !request.getDeleteImageKeys().isEmpty()) {
            post.removeImagesByKeys(request.getDeleteImageKeys());
        }

        // 새 이미지 추가 (기존 이미지 뒤에 순서대로 추가)
        if (request.getAddImageKeys() != null && !request.getAddImageKeys().isEmpty()) {
            int nextOrder = post.getImages().size();
            addImages(post, request.getAddImageKeys(), nextOrder);
        }

        // ES 재인덱싱
        eventPublisher.publishEvent(new PostIndexEvent(post, request.getHashtags()));
    }

    @Override
    public void deletePost(Long postId) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthenticatedException("게시물 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
        eventPublisher.publishEvent(new PostDeleteEvent(postId));
    }

    private void addImages(Post post, List<String> imageKeys) {
        addImages(post, imageKeys, 0);
    }

    private void addImages(Post post, List<String> imageKeys, int startOrder) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return;
        }
        IntStream.range(0, imageKeys.size())
                .forEach(i -> post.addImage(PostImage.of(post, imageKeys.get(i), startOrder + i)));
    }
}
