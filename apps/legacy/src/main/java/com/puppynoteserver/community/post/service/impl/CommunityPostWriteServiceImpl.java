package com.puppynoteserver.community.post.service.impl;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.entity.PostImage;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.CommunityPostWriteService;
import com.puppynoteserver.community.post.service.request.PostCreateServiceRequest;
import com.puppynoteserver.community.post.service.request.PostUpdateServiceRequest;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.exception.UnauthenticatedException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CommunityPostReadService communityPostReadService;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    @Override
    public Long createPost(PostCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PuppyNoteException("사용자를 찾을 수 없습니다."));

        Post post = request.toEntity(user);
        postRepository.save(post);

        addImages(post, request.getImageKeys());

        return post.getId();
    }

    @Override
    public void updatePost(Long postId, PostUpdateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        Post post = communityPostReadService.getPostOrThrow(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthenticatedException("게시물 수정 권한이 없습니다.");
        }

        post.updateContent(request.getContent());
        post.updateHashtags(request.getHashtags());

        if (request.getDeleteImageKeys() != null && !request.getDeleteImageKeys().isEmpty()) {
            post.removeImagesByKeys(request.getDeleteImageKeys());
            s3StorageService.deleteObjects(request.getDeleteImageKeys(), BucketKind.COMMUNITY_POST);
        }

        if (request.getAddImageKeys() != null && !request.getAddImageKeys().isEmpty()) {
            int nextOrder = post.getImages().size();
            addImages(post, request.getAddImageKeys(), nextOrder);
        }
    }

    @Override
    public void deletePost(Long postId) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        Post post = communityPostReadService.getPostOrThrow(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthenticatedException("게시물 삭제 권한이 없습니다.");
        }

        List<String> imageKeys = post.getImages().stream()
                .map(PostImage::getImageKey)
                .toList();

        // Logstash가 deleted_at 컬럼을 감지해 ES에서 삭제 처리
        post.softDelete();

        s3StorageService.deleteObjects(imageKeys, BucketKind.COMMUNITY_POST);
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
