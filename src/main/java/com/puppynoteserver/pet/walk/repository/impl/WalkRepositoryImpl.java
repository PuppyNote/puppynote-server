package com.puppynoteserver.pet.walk.repository.impl;

import com.puppynoteserver.pet.walk.entity.Walk;
import com.puppynoteserver.pet.walk.repository.WalkJpaRepository;
import com.puppynoteserver.pet.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WalkRepositoryImpl implements WalkRepository {

    private final WalkJpaRepository walkJpaRepository;

    @Override
    public Walk save(Walk walk) {
        return walkJpaRepository.save(walk);
    }

    @Override
    public List<Walk> findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(Long petId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return walkJpaRepository.findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(petId, startOfDay, endOfDay);
    }
}
