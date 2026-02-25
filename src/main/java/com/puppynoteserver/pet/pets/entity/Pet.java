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

    public static Pet of(String name, LocalDate birthDate, String profileImage) {
        Pet pet = new Pet();
        pet.name = name;
        pet.birthDate = birthDate;
        pet.profileImage = profileImage;
        return pet;
    }

    public void updateInfo(String name, LocalDate birthDate, String profileImage) {
        this.name = name;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }
}
