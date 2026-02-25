package com.puppynoteserver.pet.walk.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.pets.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "walks", indexes = {
        @Index(name = "idx_walks_start_time", columnList = "start_time")
})
public class Walk extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(precision = 11, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 7)
    private BigDecimal longitude;

    @Column(length = 500)
    private String memo;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkPhoto> photos = new ArrayList<>();

    public static Walk of(Pet pet, LocalDateTime startTime, LocalDateTime endTime,
                          BigDecimal latitude, BigDecimal longitude, String memo) {
        Walk walk = new Walk();
        walk.pet = pet;
        walk.startTime = startTime;
        walk.endTime = endTime;
        walk.latitude = latitude;
        walk.longitude = longitude;
        walk.memo = memo;
        return walk;
    }

    public void addPhoto(String imageKey) {
        this.photos.add(WalkPhoto.of(this, imageKey));
    }
}
