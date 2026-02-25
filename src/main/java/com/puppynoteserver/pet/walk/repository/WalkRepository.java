package com.puppynoteserver.pet.walk.repository;

import com.puppynoteserver.pet.walk.entity.Walk;

import java.time.LocalDateTime;
import java.util.List;

public interface WalkRepository {

    Walk save(Walk walk);

    List<Walk> findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(Long petId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
