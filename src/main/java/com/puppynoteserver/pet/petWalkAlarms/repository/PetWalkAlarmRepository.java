package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;

import java.util.List;
import java.util.Optional;

public interface PetWalkAlarmRepository {

    PetWalkAlarm save(PetWalkAlarm petWalkAlarm);

    Optional<PetWalkAlarm> findById(Long alarmId);

    List<PetWalkAlarm> findByPetId(Long petId);

    void delete(PetWalkAlarm petWalkAlarm);
}
