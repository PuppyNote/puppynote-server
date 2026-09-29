package com.puppynoteserver.pet.petWalkAlarms.application.port.in;

import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.response.PetWalkAlarmResponse;

public interface PetWalkAlarmWriter {

    PetWalkAlarmResponse create(PetWalkAlarmCreateServiceRequest request);

    PetWalkAlarmResponse update(PetWalkAlarmUpdateServiceRequest request);

    PetWalkAlarmResponse updateStatus(PetWalkAlarmStatusUpdateServiceRequest request);

    void delete(Long alarmId);
}
