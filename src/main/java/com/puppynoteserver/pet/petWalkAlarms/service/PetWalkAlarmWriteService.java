package com.puppynoteserver.pet.petWalkAlarms.service;

import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;

public interface PetWalkAlarmWriteService {

    PetWalkAlarmResponse create(PetWalkAlarmCreateServiceRequest request);

    PetWalkAlarmResponse update(PetWalkAlarmUpdateServiceRequest request);

    PetWalkAlarmResponse updateStatus(PetWalkAlarmStatusUpdateServiceRequest request);

    void delete(Long alarmId);
}
