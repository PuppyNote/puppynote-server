package com.puppynoteserver.walk.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.walk.entity.enums.WalkStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "walks")
public class Walk extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    private LocalDateTime startedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WalkStatus status;

    @Column(length = 255)
    private String location;

}
