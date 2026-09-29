package com.puppynoteserver.pet.walk.adapter.out.persistence;

import com.puppynoteserver.pet.walk.domain.entity.Walk;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WalkJpaRepository extends JpaRepository<Walk, Long> {

    @EntityGraph(attributePaths = {"photos"})
    List<Walk> findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(Long petId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Walk> findByPetIdAndStartTimeBetween(Long petId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"photos"})
    Optional<Walk> findWithPhotosById(Long walkId);

    long countByPetIdAndStartTimeBetween(Long petId, LocalDateTime start, LocalDateTime end);

    Optional<Walk> findTopByPetIdOrderByStartTimeDesc(Long petId);

    @EntityGraph(attributePaths = {"photos"})
    List<Walk> findAllByPetId(Long petId);
}
