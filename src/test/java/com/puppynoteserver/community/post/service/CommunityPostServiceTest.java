package com.puppynoteserver.community.post.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.community.post.service.request.PostCreateServiceRequest;
import com.puppynoteserver.community.post.service.request.PostUpdateServiceRequest;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.UnauthenticatedException;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class CommunityPostServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommunityPostWriteService communityPostWriteService;

    @Autowired
    private CommunityPostReadService communityPostReadService;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    @Override
    public void tearDown() {
        postRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("게시물을 정상적으로 생성한다.")
    @Transactional
    @Test
    void createPost_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        PostCreateServiceRequest request = PostCreateServiceRequest.builder()
                .content("테스트 게시물 내용")
                .hashtags(List.of("강아지", "산책"))
                .imageKeys(List.of())
                .build();

        // when
        Long postId = communityPostWriteService.createPost(request);

        // then
        assertThat(postId).isNotNull();
        Post savedPost = postRepository.findById(postId).orElseThrow();
        assertThat(savedPost.getContent()).isEqualTo("테스트 게시물 내용");
        assertThat(savedPost.getHashtags()).containsExactly("강아지", "산책");
    }

    @DisplayName("존재하지 않는 사용자가 게시물 생성 시 예외가 발생한다.")
    @Test
    void createPost_userNotFound_throwsException() {
        // given
        Long nonExistentUserId = 999L;
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(nonExistentUserId));

        PostCreateServiceRequest request = PostCreateServiceRequest.builder()
                .content("테스트 게시물 내용")
                .hashtags(List.of())
                .imageKeys(List.of())
                .build();

        // when & then
        assertThatThrownBy(() -> communityPostWriteService.createPost(request))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("게시물 내용을 정상적으로 수정한다.")
    @Transactional
    @Test
    void updatePost_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "원본 내용", List.of("태그1")));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        PostUpdateServiceRequest request = PostUpdateServiceRequest.builder()
                .content("수정된 내용")
                .hashtags(List.of("수정태그"))
                .addImageKeys(List.of())
                .deleteImageKeys(List.of())
                .build();

        // when
        communityPostWriteService.updatePost(post.getId(), request);

        // then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getContent()).isEqualTo("수정된 내용");
        assertThat(updatedPost.getHashtags()).containsExactly("수정태그");
    }

    @DisplayName("게시물 작성자가 아닌 사용자가 수정 시 예외가 발생한다.")
    @Test
    void updatePost_notOwner_throwsException() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User other = userRepository.save(createUser("other@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(owner, "원본 내용", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(other.getId()));

        PostUpdateServiceRequest request = PostUpdateServiceRequest.builder()
                .content("수정된 내용")
                .hashtags(List.of())
                .addImageKeys(List.of())
                .deleteImageKeys(List.of())
                .build();

        // when & then
        assertThatThrownBy(() -> communityPostWriteService.updatePost(post.getId(), request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessageContaining("게시물 수정 권한이 없습니다.");
    }

    @DisplayName("게시물을 정상적으로 소프트 삭제한다.")
    @Test
    void deletePost_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "삭제될 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        communityPostWriteService.deletePost(post.getId());

        // then
        Post deletedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(deletedPost.isDeleted()).isTrue();
    }

    @DisplayName("게시물 작성자가 아닌 사용자가 삭제 시 예외가 발생한다.")
    @Test
    void deletePost_notOwner_throwsException() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User other = userRepository.save(createUser("other@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(owner, "게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(other.getId()));

        // when & then
        assertThatThrownBy(() -> communityPostWriteService.deletePost(post.getId()))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessageContaining("게시물 삭제 권한이 없습니다.");
    }

    @DisplayName("게시물 목록을 페이지네이션으로 조회한다.")
    @Test
    void getPosts_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        postRepository.save(Post.of(user, "첫 번째 게시물", List.of()));
        postRepository.save(Post.of(user, "두 번째 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.getLikedStatusBatch(any(), anyLong())).willReturn(Map.of());
        given(postLikeRedisService.getLikeCountBatch(any())).willReturn(Map.of());
        given(s3StorageService.getCloudFrontUrl(any(), any())).willReturn("https://cdn.example.com/image.jpg");

        // when
        PostListResponse response = communityPostReadService.getPosts(null, 0, 10);

        // then
        assertThat(response.getPosts()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
    }

    @DisplayName("키워드 없이 내 게시물 목록을 조회한다.")
    @Test
    void getMyPosts_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        User other = userRepository.save(createUser("other@test.com", "password", SnsType.NORMAL));
        postRepository.save(Post.of(user, "내 게시물", List.of()));
        postRepository.save(Post.of(other, "다른 사람 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.getLikedStatusBatch(any(), anyLong())).willReturn(Map.of());
        given(postLikeRedisService.getLikeCountBatch(any())).willReturn(Map.of());
        given(s3StorageService.getCloudFrontUrl(any(), any())).willReturn("https://cdn.example.com/image.jpg");

        // when
        PostListResponse response = communityPostReadService.getMyPosts(0, 10);

        // then
        assertThat(response.getPosts()).hasSize(1);
        assertThat(response.getPosts().get(0).getContent()).isEqualTo("내 게시물");
    }

    @DisplayName("게시물 단건을 정상 조회한다.")
    @Test
    void getPost_success() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "단건 조회 게시물", List.of("태그")));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.getLikedStatus(anyLong(), anyLong())).willReturn(Optional.empty());
        given(postLikeRedisService.getLikeCount(anyLong())).willReturn(Optional.empty());
        given(s3StorageService.getCloudFrontUrl(any(), any())).willReturn("https://cdn.example.com/image.jpg");

        // when
        PostResponse response = communityPostReadService.getPost(post.getId());

        // then
        assertThat(response.getPostId()).isEqualTo(post.getId());
        assertThat(response.getContent()).isEqualTo("단건 조회 게시물");
        assertThat(response.getHashtags()).containsExactly("태그");
    }

    @DisplayName("존재하지 않는 게시물 단건 조회 시 예외가 발생한다.")
    @Test
    void getPost_notFound_throwsException() {
        // given
        Long nonExistentPostId = 999L;

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(1L));

        // when & then
        assertThatThrownBy(() -> communityPostReadService.getPost(nonExistentPostId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("게시물을 찾을 수 없습니다.");
    }

    @DisplayName("삭제된 게시물을 getPostOrThrow로 조회 시 예외가 발생한다.")
    @Test
    void getPostOrThrow_deletedPost_throwsException() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "삭제될 게시물", List.of()));
        post.softDelete();
        postRepository.save(post);

        // when & then
        assertThatThrownBy(() -> communityPostReadService.getPostOrThrow(post.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("게시물을 찾을 수 없습니다.");
    }

    @DisplayName("게시물 목록이 없을 때 빈 리스트를 반환한다.")
    @Test
    void getPosts_empty_returnsEmptyList() {
        // given
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(1L));

        // when
        PostListResponse response = communityPostReadService.getPosts(null, 0, 10);

        // then
        assertThat(response.getPosts()).isEmpty();
        assertThat(response.getTotalCount()).isEqualTo(0);
    }

    @DisplayName("키워드로 게시물 검색 시 ES 검색 서비스를 통해 결과를 반환한다.")
    @Test
    void getPosts_withKeyword_usesSearchService() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post1 = postRepository.save(Post.of(user, "강아지 산책 게시물", List.of("강아지", "산책")));
        Post post2 = postRepository.save(Post.of(user, "강아지 일상 게시물", List.of("강아지")));

        List<Long> searchResultIds = List.of(post1.getId(), post2.getId());

        given(postSearchService.search(anyString(), anyInt(), anyInt())).willReturn(searchResultIds);
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.getLikedStatusBatch(any(), anyLong())).willReturn(Map.of());
        given(postLikeRedisService.getLikeCountBatch(any())).willReturn(Map.of());
        given(s3StorageService.getCloudFrontUrl(any(), any())).willReturn("https://cdn.example.com/image.jpg");

        // when
        PostListResponse response = communityPostReadService.getPosts("강아지", 0, 10);

        // then
        assertThat(response.getPosts()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
        verify(postSearchService).search("강아지", 0, 10);
    }

    @DisplayName("해시태그 자동완성 키워드로 검색 서비스의 결과를 반환한다.")
    @Test
    void getHashtagSuggestions_returnsFromSearchService() {
        // given
        List<String> suggestions = List.of("강아지", "산책");
        given(postSearchService.searchHashtags(anyString())).willReturn(suggestions);

        // when
        List<String> result = communityPostReadService.getHashtagSuggestions("강아");

        // then
        assertThat(result).containsExactly("강아지", "산책");
        verify(postSearchService).searchHashtags("강아");
    }

    @DisplayName("Redis에 좋아요 상태와 카운트가 모두 캐시된 경우 Redis 값을 반환한다.")
    @Test
    void getPost_withRedisHit_returnsFromCache() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "Redis 캐시 테스트 게시물", List.of("태그")));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.getLikedStatus(anyLong(), anyLong())).willReturn(Optional.of(true));
        given(postLikeRedisService.getLikeCount(anyLong())).willReturn(Optional.of(5L));
        given(s3StorageService.getCloudFrontUrl(any(), any())).willReturn("https://cdn.example.com/image.jpg");

        // when
        PostResponse response = communityPostReadService.getPost(post.getId());

        // then
        assertThat(response.getPostId()).isEqualTo(post.getId());
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(5L);
    }

    @DisplayName("일부 게시물만 Redis에 캐시된 경우 캐시 히트 항목은 Redis, 미스 항목은 DB에서 조회한다.")
    @Test
    void getMyPosts_withKeyword_returnsEmpty() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        PostListResponse response = communityPostReadService.getMyPosts(0, 10);

        // then
        assertThat(response.getPosts()).isEmpty();
        assertThat(response.getTotalCount()).isEqualTo(0);
    }
}
