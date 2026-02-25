package com.puppynoteserver.pet.petWalkAlarms.service.request;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Set;

public class PetWalkAlarmUpdateServiceRequest {

    private final Long alarmId;
    private final AlarmStatus alarmStatus;
    private final Set<AlarmDay> alarmDays;
    private final LocalTime alarmTime;

    @Builder
    private PetWalkAlarmUpdateServiceRequest(Long alarmId, AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        this.alarmId = alarmId;
        this.alarmStatus = alarmStatus;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void applyTo(PetWalkAlarm petWalkAlarm) {
        petWalkAlarm.update(alarmStatus, alarmDays, alarmTime);
    }
}
