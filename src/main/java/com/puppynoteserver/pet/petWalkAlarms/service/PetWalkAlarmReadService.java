package com.puppynoteserver.pet.petWalkAlarms.service;

import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;

import java.util.List;

public interface PetWalkAlarmReadService {

    List<PetWalkAlarmResponse> getAlarmsByPetId(Long petId);

    List<java.time.LocalTime> getTodayAlarmTimes(Long petId);
}
