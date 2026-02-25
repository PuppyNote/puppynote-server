package com.puppynoteserver.pet.petWalkAlarms.service.response;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import lombok.Getter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PetWalkAlarmResponse {

    private final Long alarmId;
    private final AlarmStatus alarmStatus;
    private final Set<AlarmDay> alarmDays;
    private final LocalTime alarmTime;

    private PetWalkAlarmResponse(Long alarmId, AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        this.alarmId = alarmId;
        this.alarmStatus = alarmStatus;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
    }

    public static PetWalkAlarmResponse from(PetWalkAlarm petWalkAlarm) {
        return new PetWalkAlarmResponse(
                petWalkAlarm.getId(),
                petWalkAlarm.getAlarmStatus(),
                new HashSet<>(petWalkAlarm.getAlarmDays()),
                petWalkAlarm.getAlarmTime()
        );
    }
}
