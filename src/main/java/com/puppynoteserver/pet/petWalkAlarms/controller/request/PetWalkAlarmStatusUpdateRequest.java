package com.puppynoteserver.pet.petWalkAlarms.controller.request;

import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmStatusUpdateServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PetWalkAlarmStatusUpdateRequest {

    @NotNull(message = "알람 ID는 필수입니다.")
    private Long alarmId;

    @NotNull(message = "알람 활성화 여부는 필수입니다.")
    private AlarmStatus alarmStatus;

    @Builder
    private PetWalkAlarmStatusUpdateRequest(Long alarmId, AlarmStatus alarmStatus) {
        this.alarmId = alarmId;
        this.alarmStatus = alarmStatus;
    }

    public PetWalkAlarmStatusUpdateServiceRequest toServiceRequest() {
        return PetWalkAlarmStatusUpdateServiceRequest.builder()
                .alarmId(alarmId)
                .alarmStatus(alarmStatus)
                .build();
    }
}
