package com.puppynoteserver.community.post.service.impl;

import com.puppynoteserver.community.like.repository.PostLikeRepository;
import com.puppynoteserver.community.like.repository.dto.PostLikeAggDto;
import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.entity.PostImage;
import com.puppynoteserver.community.post.repository.PostImageRepository;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.PostSearchService;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.UnauthenticatedException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.redis.PostLikeRedisService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostReadServiceImpl implements CommunityPostReadService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeRedisService postLikeRedisService;
    private final S3StorageService s3StorageService;
    private final SecurityService securityService;
    private final PostSearchService postSearchService;

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
    public PostListResponse getMyPosts(int page, int size) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        Page<Post> postPage = postRepository.findAllByUserIdWithUser(userId, PageRequest.of(page, size));

        List<PostResponse> responses = toPostResponses(postPage.getContent());
        return PostListResponse.of(responses, page, postPage.getTotalPages(), postPage.getTotalElements());
    }

    @Override
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다."));

        Long currentUserId = getCurrentUserIdSafely();

        List<String> imageKeys = extractImageKeys(post);
        List<String> imageUrls = buildImageUrls(post);
        String userProfileUrl = buildUserProfileUrl(post);
        LikeInfo likeInfo = resolveLikeInfo(postId, currentUserId);

        return PostResponse.of(post, userProfileUrl, imageKeys, imageUrls, likeInfo.count(), likeInfo.liked());
    }

    @Override
    public Post getPostOrThrow(Long postId) {
        return postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다."));
    }

    @Override
    public List<String> getHashtagSuggestions(String keyword) {
        if (postSearchService == null) {
            return List.of();
        }
        return postSearchService.searchHashtags(keyword);
    }

    // 게시물 목록을 PostResponse로 변환 (N+1 방지: 이미지/좋아요 일괄 조회)
    private List<PostResponse> toPostResponses(List<Post> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }

        Long currentUserId = getCurrentUserIdSafely();
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        Map<Long, List<PostImage>> imageMap = buildImageMap(postIds);
        Map<Long, LikeInfo> likeInfoMap = resolveLikeInfoBatch(postIds, currentUserId);

        return posts.stream()
                .map(post -> buildPostResponse(post, imageMap, likeInfoMap))
                .collect(Collectors.toList());
    }

    // 단건 게시물의 이미지 키 목록을 추출한다
    private List<String> extractImageKeys(Post post) {
        return post.getImages().stream()
                .map(PostImage::getImageKey)
                .collect(Collectors.toList());
    }

    // 단건 게시물의 이미지 CloudFront URL 목록을 생성한다
    private List<String> buildImageUrls(Post post) {
        return post.getImages().stream()
                .map(img -> s3StorageService.getCloudFrontUrl(img.getImageKey(), BucketKind.COMMUNITY_POST))
                .collect(Collectors.toList());
    }

    // 게시물 작성자의 프로필 CloudFront URL을 생성한다
    private String buildUserProfileUrl(Post post) {
        return s3StorageService.getCloudFrontUrl(post.getUser().getProfileUrl(), BucketKind.USER_PROFILE);
    }

    // postId 목록으로 이미지 맵을 일괄 조회한다 (key: postId, value: 해당 게시물의 이미지 목록)
    private Map<Long, List<PostImage>> buildImageMap(List<Long> postIds) {
        return postImageRepository.findByPostIdIn(postIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getPost().getId()));
    }

    // 단건 게시물의 좋아요 정보를 조회한다 (Redis 우선 → 캐시 미스 시 DB fallback)
    private LikeInfo resolveLikeInfo(Long postId, Long currentUserId) {
        Optional<Boolean> cachedLiked = currentUserId != null
                ? postLikeRedisService.getLikedStatus(postId, currentUserId)
                : Optional.empty();
        Optional<Long> cachedCount = postLikeRedisService.getLikeCount(postId);

        if (cachedLiked.isPresent() && cachedCount.isPresent()) {
            return new LikeInfo(cachedCount.get(), cachedLiked.get());
        }

        // Redis 미스 → DB에서 좋아요 수 및 좋아요 여부 조회
        PostLikeAggDto likeAgg = postLikeRepository
                .findLikeAggByPostIds(List.of(postId), currentUserId != null ? currentUserId : 0L)
                .stream().findFirst().orElse(null);

        return new LikeInfo(
                likeAgg != null ? likeAgg.getLikeCount() : 0L,
                likeAgg != null && likeAgg.getIsLikedCount() > 0
        );
    }

    // 다건 게시물의 좋아요 정보를 일괄 조회한다 (Redis MGET 우선 → 캐시 미스 postId만 DB fallback)
    private Map<Long, LikeInfo> resolveLikeInfoBatch(List<Long> postIds, Long currentUserId) {
        Map<Long, Boolean> redisLikedMap = currentUserId != null
                ? postLikeRedisService.getLikedStatusBatch(postIds, currentUserId)
                : Map.of();
        Map<Long, Long> redisCountMap = postLikeRedisService.getLikeCountBatch(postIds);

        // Redis에서 liked와 count 둘 다 hit한 postId는 DB 조회 제외
        List<Long> dbFallbackIds = postIds.stream()
                .filter(id -> !redisLikedMap.containsKey(id) || !redisCountMap.containsKey(id))
                .toList();

        Map<Long, PostLikeAggDto> likeAggMap = new HashMap<>();
        if (!dbFallbackIds.isEmpty()) {
            postLikeRepository.findLikeAggByPostIds(dbFallbackIds, currentUserId != null ? currentUserId : 0L)
                    .forEach(dto -> likeAggMap.put(dto.getPostId(), dto));
        }

        Map<Long, LikeInfo> result = new HashMap<>();
        for (Long postId : postIds) {
            if (redisLikedMap.containsKey(postId) && redisCountMap.containsKey(postId)) {
                // Redis hit → Redis 값 사용
                result.put(postId, new LikeInfo(redisCountMap.get(postId), redisLikedMap.get(postId)));
            } else {
                // Redis miss → DB 값 사용
                PostLikeAggDto likeAgg = likeAggMap.get(postId);
                result.put(postId, new LikeInfo(
                        likeAgg != null ? likeAgg.getLikeCount() : 0L,
                        likeAgg != null && likeAgg.getIsLikedCount() > 0
                ));
            }
        }
        return result;
    }

    // 게시물 목록 변환 시 단건 PostResponse를 조립한다
    private PostResponse buildPostResponse(Post post, Map<Long, List<PostImage>> imageMap, Map<Long, LikeInfo> likeInfoMap) {
        Long pid = post.getId();
        List<PostImage> images = imageMap.getOrDefault(pid, List.of());

        List<String> imageKeys = images.stream()
                .map(PostImage::getImageKey)
                .collect(Collectors.toList());

        List<String> imageUrls = images.stream()
                .map(img -> s3StorageService.getCloudFrontUrl(img.getImageKey(), BucketKind.COMMUNITY_POST))
                .collect(Collectors.toList());

        String userProfileUrl = buildUserProfileUrl(post);
        LikeInfo likeInfo = likeInfoMap.get(pid);

        return PostResponse.of(post, userProfileUrl, imageKeys, imageUrls, likeInfo.count(), likeInfo.liked());
    }

    private Long getCurrentUserIdSafely() {
        try {
            return securityService.getCurrentLoginUserInfo().getUserId();
        } catch (UnauthenticatedException e) {
            return null;
        }
    }

    // 좋아요 수와 좋아요 여부를 묶은 값 객체
    private record LikeInfo(long count, boolean liked) {
    }
}
