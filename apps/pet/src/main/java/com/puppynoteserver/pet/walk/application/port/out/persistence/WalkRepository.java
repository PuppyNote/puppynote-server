package com.puppynoteserver.pet.walk.application.port.out.persistence;

import com.puppynoteserver.pet.walk.domain.entity.Walk;

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

    List<Walk> findAllByPetId(Long petId);

    void deleteById(Long walkId);

    void deleteAllByPetId(Long petId);
}
