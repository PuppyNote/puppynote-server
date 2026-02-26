package com.puppynoteserver.alertHistory.service.alertHistory.request;


import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class AlertHistoryDeviceIdServiceRequest extends PageInfoServiceRequest {

	private String deviceId;

}

