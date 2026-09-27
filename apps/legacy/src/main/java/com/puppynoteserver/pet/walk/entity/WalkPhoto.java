package com.puppynoteserver.pet.walk.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "walk_photos")
public class WalkPhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id", nullable = false)
    private Walk walk;

    @Column(nullable = false, length = 255)
    private String imageKey;

    public static WalkPhoto of(Walk walk, String imageKey) {
        WalkPhoto walkPhoto = new WalkPhoto();
        walkPhoto.walk = walk;
        walkPhoto.imageKey = imageKey;
        return walkPhoto;
    }
}
