package com.puppynoteserver.pet.petWalkAlarms.application.port.in.request;

import com.puppynoteserver.pet.petWalkAlarms.domain.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmStatus;
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
