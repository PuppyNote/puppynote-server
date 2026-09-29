package com.puppynoteserver.pet.petWalkAlarms.application.port.out.persistence;

import com.puppynoteserver.pet.petWalkAlarms.domain.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmStatus;

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

    void deleteAllByPetId(Long petId);
}
