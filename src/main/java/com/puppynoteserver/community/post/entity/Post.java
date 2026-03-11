package com.puppynoteserver.community.post.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNum ASC")
    private List<PostImage> images = new ArrayList<>();

    public static Post of(User user, String content) {
        Post post = new Post();
        post.user = user;
        post.content = content;
        return post;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void clearImages() {
        this.images.clear();
    }

    public void addImage(PostImage image) {
        this.images.add(image);
    }
}
