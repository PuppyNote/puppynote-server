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

    @ElementCollection
    @CollectionTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNum ASC")
    private List<PostImage> images = new ArrayList<>();

    public static Post of(User user, String content, List<String> hashtags) {
        Post post = new Post();
        post.user = user;
        post.content = content;
        if (hashtags != null) {
            post.hashtags.addAll(hashtags);
        }
        return post;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateHashtags(List<String> hashtags) {
        this.hashtags.clear();
        if (hashtags != null) {
            this.hashtags.addAll(hashtags);
        }
    }

    public void removeImagesByKeys(List<String> imageKeys) {
        this.images.removeIf(img -> imageKeys.contains(img.getImageKey()));
    }

    public void addImage(PostImage image) {
        this.images.add(image);
    }
}
