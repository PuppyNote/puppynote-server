package com.puppynoteserver.pet.petItems.repository;

import com.puppynoteserver.pet.petItems.entity.PetItemPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetItemPurchaseJpaRepository extends JpaRepository<PetItemPurchase, Long> {

    // 특정 petItem들의 가장 최근 구매일 조회 (1건씩)
    @Query("""
            SELECT p FROM PetItemPurchase p
            WHERE p.petItem.id IN :petItemIds
              AND p.purchasedAt = (
                  SELECT MAX(p2.purchasedAt) FROM PetItemPurchase p2
                  WHERE p2.petItem.id = p.petItem.id
              )
            """)
    List<PetItemPurchase> findLatestByPetItemIds(@Param("petItemIds") List<Long> petItemIds);

    Optional<PetItemPurchase> findTopByPetItemIdOrderByPurchasedAtDesc(Long petItemId);
}
