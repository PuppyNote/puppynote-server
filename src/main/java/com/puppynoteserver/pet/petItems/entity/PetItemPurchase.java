package com.puppynoteserver.pet.petItems.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_item_purchases", indexes = {
        @Index(name = "idx_pet_item_purchases_pet_item_id", columnList = "pet_item_id")
})
public class PetItemPurchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_item_id", nullable = false)
    private PetItem petItem;

    @Column(nullable = false)
    private LocalDate purchasedAt;

    public static PetItemPurchase of(PetItem petItem, LocalDate purchasedAt) {
        PetItemPurchase purchase = new PetItemPurchase();
        purchase.petItem = petItem;
        purchase.purchasedAt = purchasedAt;
        return purchase;
    }
}
