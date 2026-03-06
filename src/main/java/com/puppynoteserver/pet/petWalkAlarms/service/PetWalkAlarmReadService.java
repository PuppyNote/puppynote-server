package com.puppynoteserver.pet.petWalkAlarms.service;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;

import java.util.List;

public interface PetWalkAlarmReadService {

    PetWalkAlarm findById(Long alarmId);

    List<PetWalkAlarmResponse> getAlarmsByPetId(Long petId);

    List<java.time.LocalTime> getTodayAlarmTimes(Long petId);
}
