package com.puppynoteserver.community.post.like.repository;

import com.puppynoteserver.community.post.like.entity.PostLike;
import com.puppynoteserver.community.post.like.repository.dto.PostLikeAggDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.postId = :postId AND pl.userId = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT pl.userId FROM PostLike pl WHERE pl.postId = :postId")
    Set<Long> findUserIdsByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.postId = :postId AND pl.userId IN :userIds")
    void deleteByPostIdAndUserIdIn(@Param("postId") Long postId, @Param("userIds") Collection<Long> userIds);

    // 단건 카운트 (캐시 미스 시 단일 게시물 카운트 조회)
    long countByPostId(Long postId);

    // 게시물 목록 조회 시 좋아요 수 + 좋아요 여부 한 번에 조회
    @Query("""
            SELECT new com.puppynoteserver.community.post.like.repository.dto.PostLikeAggDto(
                pl.postId,
                COUNT(pl),
                SUM(CASE WHEN pl.userId = :userId THEN 1 ELSE 0 END)
            )
            FROM PostLike pl
            WHERE pl.postId IN :postIds
            GROUP BY pl.postId
            """)
    List<PostLikeAggDto> findLikeAggByPostIds(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);
}
