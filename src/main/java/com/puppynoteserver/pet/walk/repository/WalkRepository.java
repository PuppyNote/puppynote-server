package com.puppynoteserver.pet.walk.repository;

import com.puppynoteserver.pet.walk.entity.Walk;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WalkRepository {

    Walk save(Walk walk);

    Optional<Walk> findById(Long walkId);

    List<Walk> findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(Long petId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Walk> findByPetIdAndStartTimeBetween(Long petId, LocalDateTime start, LocalDateTime end);

    long countByPetIdAndStartTimeBetween(Long petId, LocalDateTime start, LocalDateTime end);

    Optional<Walk> findTopByPetIdOrderByStartTimeDesc(Long petId);

    void deleteById(Long walkId);

    void deleteAllByPetId(Long petId);
}
