package com.puppynoteserver.pet.petWalkAlarms.repository.impl;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmJpaRepository;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetWalkAlarmRepositoryImpl implements PetWalkAlarmRepository {

    private final PetWalkAlarmJpaRepository petWalkAlarmJpaRepository;

    @Override
    public PetWalkAlarm save(PetWalkAlarm petWalkAlarm) {
        return petWalkAlarmJpaRepository.save(petWalkAlarm);
    }

    @Override
    public Optional<PetWalkAlarm> findById(Long alarmId) {
        return petWalkAlarmJpaRepository.findById(alarmId);
    }

    @Override
    public void delete(PetWalkAlarm petWalkAlarm) {
        petWalkAlarmJpaRepository.delete(petWalkAlarm);
    }
}
