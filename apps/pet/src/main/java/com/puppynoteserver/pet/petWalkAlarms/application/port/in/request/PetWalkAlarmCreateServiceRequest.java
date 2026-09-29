package com.puppynoteserver.pet.petWalkAlarms.application.port.in.request;

import com.puppynoteserver.pet.petWalkAlarms.domain.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Set;

public class PetWalkAlarmCreateServiceRequest {

    private final Long petId;
    private final AlarmStatus alarmStatus;
    private final Set<AlarmDay> alarmDays;
    private final LocalTime alarmTime;

    @Builder
    private PetWalkAlarmCreateServiceRequest(Long petId, AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        this.petId = petId;
        this.alarmStatus = alarmStatus;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
    }

    public Long getPetId() {
        return petId;
    }

    public PetWalkAlarm toEntity(Pet pet) {
        return PetWalkAlarm.of(pet, alarmStatus, alarmDays, alarmTime);
    }
}
