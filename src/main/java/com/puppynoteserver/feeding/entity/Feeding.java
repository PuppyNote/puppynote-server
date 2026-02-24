package com.puppynoteserver.feeding.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedings")
public class Feeding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private LocalDateTime fedAt;

    @Column(length = 100)
    private String foodType;

    @Column(length = 50)
    private String amount;

    public static Feeding of(Pet pet, User user, LocalDateTime fedAt, String foodType, String amount) {
        Feeding feeding = new Feeding();
        feeding.pet = pet;
        feeding.user = user;
        feeding.fedAt = fedAt;
        feeding.foodType = foodType;
        feeding.amount = amount;
        return feeding;
    }
}
