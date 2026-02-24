package com.puppynoteserver.pet.pets.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pets")
public class Pet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String breed;

    private LocalDate birthDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(length = 255)
    private String profileImageUrl;

    public static Pet of(String name, String breed, LocalDate birthDate, BigDecimal weight, String profileImageUrl) {
        Pet pet = new Pet();
        pet.name = name;
        pet.breed = breed;
        pet.birthDate = birthDate;
        pet.weight = weight;
        pet.profileImageUrl = profileImageUrl;
        return pet;
    }

    public void updateInfo(String name, String breed, LocalDate birthDate, BigDecimal weight, String profileImageUrl) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.weight = weight;
        this.profileImageUrl = profileImageUrl;
    }
}
