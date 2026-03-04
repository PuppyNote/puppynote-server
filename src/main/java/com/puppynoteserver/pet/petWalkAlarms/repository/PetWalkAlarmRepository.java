package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PetWalkAlarmRepository {

    PetWalkAlarm save(PetWalkAlarm petWalkAlarm);

    Optional<PetWalkAlarm> findById(Long alarmId);

    List<PetWalkAlarm> findByPetId(Long petId);

    void delete(PetWalkAlarm petWalkAlarm);

    List<PetWalkAlarm> findActiveAlarmsAtTimeAndDay(AlarmStatus status, LocalTime time, AlarmDay day);

    List<PetWalkAlarm> findTodayAlarmsByPetId(Long petId, AlarmStatus status, AlarmDay day);
}
