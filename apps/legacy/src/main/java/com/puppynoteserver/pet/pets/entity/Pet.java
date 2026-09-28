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

    private LocalDate birthDate;

    @Column(length = 255)
    private String profileImage;

    @Column(length = 50)
    private String registrationNumber;

    public static Pet of(String name, LocalDate birthDate, String profileImage, String registrationNumber) {
        Pet pet = new Pet();
        pet.name = name;
        pet.birthDate = birthDate;
        pet.profileImage = profileImage;
        pet.registrationNumber = registrationNumber;
        return pet;
    }

    public void updateInfo(String name, LocalDate birthDate, String profileImage, String registrationNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.registrationNumber = registrationNumber;
    }
}
