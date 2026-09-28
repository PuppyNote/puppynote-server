package com.puppynoteserver.alertSetting.service.request;

import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertSettingUpdateServiceRequest {

	private final AlertType all;
	private final AlertType walk;
	private final AlertType friend;

	@Builder
	public AlertSettingUpdateServiceRequest(AlertType all, AlertType walk, AlertType friend) {
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}
}
