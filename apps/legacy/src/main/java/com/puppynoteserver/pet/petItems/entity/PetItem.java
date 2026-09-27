package com.puppynoteserver.pet.petItems.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.pets.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_items")
public class PetItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ItemCategory category;

    @Column(nullable = false)
    private int purchaseCycleDays;

    @Column(length = 500)
    private String purchaseUrl;

    @Column(length = 255)
    private String imageKey;

    public static PetItem of(Pet pet, String name, ItemCategory category,
                             int purchaseCycleDays, String purchaseUrl, String imageKey) {
        PetItem petItem = new PetItem();
        petItem.pet = pet;
        petItem.name = name;
        petItem.category = category;
        petItem.purchaseCycleDays = purchaseCycleDays;
        petItem.purchaseUrl = purchaseUrl;
        petItem.imageKey = imageKey;
        return petItem;
    }

    public void update(String name, ItemCategory category, int purchaseCycleDays,
                       String purchaseUrl, String imageKey) {
        this.name = name;
        this.category = category;
        this.purchaseCycleDays = purchaseCycleDays;
        this.purchaseUrl = purchaseUrl;
        this.imageKey = imageKey;
    }
}
