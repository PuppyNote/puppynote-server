package com.puppynoteserver.community.like.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.community.like.entity.PostLike;
import com.puppynoteserver.community.like.repository.PostLikeRepository;
import com.puppynoteserver.community.like.service.response.PostLikeToggleResponse;
import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.repository.PostRepository;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class PostLikeServiceTest extends IntegrationTestSupport {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @AfterEach
    @Override
    public void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("좋아요가 없는 게시물에 좋아요 토글 시 liked=true를 반환한다.")
    @Test
    void toggleLike_whenNotLiked_returnsLikedTrue() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "테스트 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.existsCountCache(post.getId())).willReturn(false);
        given(postLikeRedisService.existsLikedCache(user.getId(), post.getId())).willReturn(false);
        given(postLikeRedisService.toggle(user.getId(), post.getId())).willReturn(List.of(1L, 1L));

        // when
        PostLikeToggleResponse response = postLikeService.toggleLike(post.getId());

        // then
        assertThat(response)
                .extracting("liked", "likeCount")
                .containsExactly(true, 1L);
    }

    @DisplayName("좋아요가 있는 게시물에 좋아요 토글 시 liked=false를 반환한다.")
    @Test
    void toggleLike_whenAlreadyLiked_returnsLikedFalse() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "테스트 게시물", List.of()));
        postLikeRepository.save(PostLike.of(post.getId(), user.getId()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.existsCountCache(post.getId())).willReturn(true);
        given(postLikeRedisService.existsLikedCache(user.getId(), post.getId())).willReturn(true);
        given(postLikeRedisService.toggle(user.getId(), post.getId())).willReturn(List.of(0L, 0L));

        // when
        PostLikeToggleResponse response = postLikeService.toggleLike(post.getId());

        // then
        assertThat(response)
                .extracting("liked", "likeCount")
                .containsExactly(false, 0L);
    }

    @DisplayName("캐시 미스 시 DB에서 카운트와 좋아요 여부를 초기화한다.")
    @Test
    void toggleLike_cacheMiss_initializesFromDb() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "테스트 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.existsCountCache(post.getId())).willReturn(false);
        given(postLikeRedisService.existsLikedCache(user.getId(), post.getId())).willReturn(false);
        given(postLikeRedisService.toggle(user.getId(), post.getId())).willReturn(List.of(1L, 1L));

        // when
        PostLikeToggleResponse response = postLikeService.toggleLike(post.getId());

        // then
        assertThat(response).isNotNull();
    }

    @DisplayName("존재하지 않는 게시물 ID로 좋아요 토글 시 예외가 발생한다.")
    @Test
    void toggleLike_postNotFound_throwsException() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Long nonExistentPostId = 999L;

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when & then
        assertThatThrownBy(() -> postLikeService.toggleLike(nonExistentPostId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("게시물을 찾을 수 없습니다.");
    }

    @DisplayName("Redis 카운트 캐시는 있지만 좋아요 캐시가 없을 때도 정상 동작한다.")
    @Test
    void toggleLike_countCacheHitLikedCacheMiss() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Post post = postRepository.save(Post.of(user, "테스트 게시물", List.of()));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));
        given(postLikeRedisService.existsCountCache(post.getId())).willReturn(true);
        given(postLikeRedisService.existsLikedCache(user.getId(), post.getId())).willReturn(false);
        given(postLikeRedisService.toggle(user.getId(), post.getId())).willReturn(List.of(1L, 1L));

        // when
        PostLikeToggleResponse response = postLikeService.toggleLike(post.getId());

        // then
        assertThat(response)
                .extracting("liked", "likeCount")
                .containsExactly(true, 1L);
    }
}
