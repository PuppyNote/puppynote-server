package com.puppynoteserver.community.post.service.impl;

import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.entity.PostImage;
import com.puppynoteserver.community.post.repository.PostImageRepository;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.PostSearchService;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostReadServiceImpl implements CommunityPostReadService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final S3StorageService s3StorageService;

    // 테스트 환경에서는 null (ES 미사용)
    @Autowired(required = false)
    private PostSearchService postSearchService;

    @Override
    public PostListResponse getPosts(String keyword, int page, int size) {
        List<Post> posts;
        int totalPages;
        long totalCount;

        if (keyword != null && !keyword.isBlank() && postSearchService != null) {
            // ES 검색 → postId 목록 → MySQL 조회
            List<Long> postIds = postSearchService.search(keyword, page, size);
            posts = postRepository.findByIdInWithUser(postIds);
            totalPages = 1;
            totalCount = posts.size();

            List<PostResponse> responses = toPostResponses(posts);
            return PostListResponse.of(responses, page, totalPages, totalCount);
        }
        // MySQL 전체 목록 페이징
        Page<Post> postPage = postRepository.findAllWithUser(PageRequest.of(page, size));
        posts = postPage.getContent();
        totalPages = postPage.getTotalPages();
        totalCount = postPage.getTotalElements();

        List<PostResponse> responses = toPostResponses(posts);
        return PostListResponse.of(responses, page, totalPages, totalCount);
    }

    @Override
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다."));

        List<String> imageUrls = post.getImages().stream()
                .map(img -> s3StorageService.createPresignedUrl(img.getImageKey(), BucketKind.COMMUNITY_POST))
                .collect(Collectors.toList());

        String userProfileUrl = s3StorageService.createPresignedUrl(
                post.getUser().getProfileUrl(), BucketKind.USER_PROFILE);

        return PostResponse.of(post, userProfileUrl, imageUrls);
    }

    @Override
    public List<String> getHashtagSuggestions(String keyword) {
        if (postSearchService == null) {
            return List.of();
        }
        return postSearchService.searchHashtags(keyword);
    }

    // 게시물 목록을 PostResponse로 변환 (N+1 방지: 이미지 일괄 조회)
    private List<PostResponse> toPostResponses(List<Post> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        // 이미지 일괄 조회 후 postId 기준으로 그룹핑
        Map<Long, List<PostImage>> imageMap = postImageRepository.findByPostIdIn(postIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getPost().getId()));

        return posts.stream()
                .map(post -> {
                    List<String> imageUrls = imageMap.getOrDefault(post.getId(), List.of())
                            .stream()
                            .map(img -> s3StorageService.createPresignedUrl(img.getImageKey(), BucketKind.COMMUNITY_POST))
                            .collect(Collectors.toList());

                    String userProfileUrl = s3StorageService.createPresignedUrl(
                            post.getUser().getProfileUrl(), BucketKind.USER_PROFILE);

                    return PostResponse.of(post, userProfileUrl, imageUrls);
                })
                .collect(Collectors.toList());
    }
}
