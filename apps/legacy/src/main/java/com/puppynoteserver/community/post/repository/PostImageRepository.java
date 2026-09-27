package com.puppynoteserver.community.post.repository;

import com.puppynoteserver.community.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("SELECT pi FROM PostImage pi WHERE pi.post.id IN :postIds ORDER BY pi.post.id, pi.orderNum ASC")
    List<PostImage> findByPostIdIn(@Param("postIds") List<Long> postIds);
}
