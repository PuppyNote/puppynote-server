package com.puppynoteserver.pet.petWalkAlarms.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmUpdateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Getter
@NoArgsConstructor
public class PetWalkAlarmUpdateRequest {

    @NotNull(message = "알람 ID는 필수입니다.")
    private Long alarmId;

    @NotNull(message = "알람 활성화 여부는 필수입니다.")
    private AlarmStatus alarmStatus;

    @NotEmpty(message = "알람 요일은 하나 이상 선택해야 합니다.")
    private Set<AlarmDay> alarmDays;

    @NotNull(message = "알람 시간은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime alarmTime;

    @Builder
    private PetWalkAlarmUpdateRequest(Long alarmId, AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        this.alarmId = alarmId;
        this.alarmStatus = alarmStatus;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
    }

    public PetWalkAlarmUpdateServiceRequest toServiceRequest() {
        return PetWalkAlarmUpdateServiceRequest.builder()
                .alarmId(alarmId)
                .alarmStatus(alarmStatus)
                .alarmDays(alarmDays)
                .alarmTime(alarmTime)
                .build();
    }
}
