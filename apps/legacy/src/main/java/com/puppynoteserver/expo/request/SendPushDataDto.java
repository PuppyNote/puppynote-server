package com.puppynoteserver.expo.request;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendPushDataDto {

    private AlertDestinationType alert_destination_type;
    private String alert_destination_info;

    @Builder
    private SendPushDataDto(AlertDestinationType alert_destination_type, String alert_destination_info) {
        this.alert_destination_type = alert_destination_type;
        this.alert_destination_info = alert_destination_info;
    }
}
