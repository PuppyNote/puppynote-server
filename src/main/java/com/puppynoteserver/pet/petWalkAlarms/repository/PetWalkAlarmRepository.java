package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;

import java.util.Optional;

public interface PetWalkAlarmRepository {

    PetWalkAlarm save(PetWalkAlarm petWalkAlarm);

    Optional<PetWalkAlarm> findById(Long alarmId);

    void delete(PetWalkAlarm petWalkAlarm);
}
