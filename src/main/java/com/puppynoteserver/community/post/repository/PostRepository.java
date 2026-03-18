package com.puppynoteserver.community.post.repository;

import com.puppynoteserver.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.deletedAt IS NULL ORDER BY p.createdDate DESC")
    Page<Post> findAllWithUser(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :postId AND p.deletedAt IS NULL")
    Optional<Post> findByIdWithUser(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id IN :ids AND p.deletedAt IS NULL ORDER BY p.createdDate DESC")
    List<Post> findByIdInWithUser(@Param("ids") List<Long> ids);
}
