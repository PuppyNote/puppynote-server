package com.puppynoteserver.pet.petWalkAlarms.application.port.in;

import com.puppynoteserver.pet.petWalkAlarms.application.port.in.response.PetWalkAlarmResponse;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.PetWalkAlarm;

import java.time.LocalTime;
import java.util.List;

public interface PetWalkAlarmFinder {

    PetWalkAlarm findById(Long alarmId);

    List<PetWalkAlarmResponse> getAlarmsByPetId(Long petId);

    List<LocalTime> getTodayAlarmTimes(Long petId);
}
