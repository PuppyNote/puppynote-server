package com.puppynoteserver.supply.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.supply.entity.enums.SupplyCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "supplies")
public class Supply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SupplyCategory category;

    private Integer quantity;

    private Integer reorderPoint;

    private LocalDate lastPurchasedDate;

    private Integer purchaseCycleDays;

    public static Supply of(Pet pet, String name, SupplyCategory category, Integer quantity, Integer reorderPoint) {
        Supply supply = new Supply();
        supply.pet = pet;
        supply.name = name;
        supply.category = category;
        supply.quantity = quantity;
        supply.reorderPoint = reorderPoint;
        return supply;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateLastPurchasedDate(LocalDate lastPurchasedDate) {
        this.lastPurchasedDate = lastPurchasedDate;
    }

    public boolean isReorderNeeded() {
        return this.quantity != null && this.reorderPoint != null && this.quantity <= this.reorderPoint;
    }
}
