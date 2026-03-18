package com.puppynoteserver.community.post.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_images")
public class PostImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageKey;

    @Column(nullable = false)
    private int orderNum;

    public static PostImage of(Post post, String imageKey, int orderNum) {
        PostImage postImage = new PostImage();
        postImage.post = post;
        postImage.imageKey = imageKey;
        postImage.orderNum = orderNum;
        return postImage;
    }
}
