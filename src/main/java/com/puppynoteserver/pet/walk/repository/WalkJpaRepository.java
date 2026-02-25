package com.puppynoteserver.pet.walk.repository;

import com.puppynoteserver.pet.walk.entity.Walk;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WalkJpaRepository extends JpaRepository<Walk, Long> {

    @EntityGraph(attributePaths = {"photos"})
    List<Walk> findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(Long petId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
