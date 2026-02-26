package com.puppynoteserver.alertSetting.service.response;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertSettingResponse {

	private final AlertType all;
	private final AlertType walk;
	private final AlertType friend;

	@Builder
	private AlertSettingResponse(AlertType all, AlertType walk, AlertType friend) {
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}

	public static AlertSettingResponse createResponse(AlertSetting alertSetting) {
		return AlertSettingResponse.builder()
			.all(alertSetting.getAll())
			.walk(alertSetting.getWalk())
			.friend(alertSetting.getFriend())
			.build();
	}
}
