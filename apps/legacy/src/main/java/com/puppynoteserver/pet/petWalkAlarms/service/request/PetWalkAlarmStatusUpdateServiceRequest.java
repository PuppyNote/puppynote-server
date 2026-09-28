package com.puppynoteserver.pet.petWalkAlarms.service.request;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import lombok.Builder;

public class PetWalkAlarmStatusUpdateServiceRequest {

    private final Long alarmId;
    private final AlarmStatus alarmStatus;

    @Builder
    private PetWalkAlarmStatusUpdateServiceRequest(Long alarmId, AlarmStatus alarmStatus) {
        this.alarmId = alarmId;
        this.alarmStatus = alarmStatus;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void applyTo(PetWalkAlarm petWalkAlarm) {
        petWalkAlarm.updateStatus(alarmStatus);
    }
}
