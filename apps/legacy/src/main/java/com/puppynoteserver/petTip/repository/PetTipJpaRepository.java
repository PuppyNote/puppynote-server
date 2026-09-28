package com.puppynoteserver.petTip.repository;

import com.puppynoteserver.petTip.entity.PetTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PetTipJpaRepository extends JpaRepository<PetTip, Long> {

    @Query(value = "SELECT * FROM pet_tips ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<PetTip> findOneRandom();
}
