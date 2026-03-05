package com.puppynoteserver.pet.petItemPurchase.repository;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
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

    List<PetItemPurchase> findAllByPetItemIdOrderByPurchasedAtDesc(Long petItemId);

    // 모든 petItem의 가장 최근 구매이력 조회 (구매주기 알림 배치용)
    @Query("""
            SELECT p FROM PetItemPurchase p
            JOIN FETCH p.petItem pi
            JOIN FETCH pi.pet
            WHERE p.purchasedAt = (
                SELECT MAX(p2.purchasedAt) FROM PetItemPurchase p2 WHERE p2.petItem.id = pi.id
            )
            """)
    List<PetItemPurchase> findAllLatestPurchases();

    @Query("""
            SELECT p FROM PetItemPurchase p
            JOIN FETCH p.petItem pi
            WHERE pi.pet.id = :petId
              AND p.purchasedAt = (
                  SELECT MAX(p2.purchasedAt) FROM PetItemPurchase p2 WHERE p2.petItem.id = pi.id
              )
            """)
    List<PetItemPurchase> findLatestPurchasesByPetId(@Param("petId") Long petId);

    void deleteAllByPetItemId(Long petItemId);
}
