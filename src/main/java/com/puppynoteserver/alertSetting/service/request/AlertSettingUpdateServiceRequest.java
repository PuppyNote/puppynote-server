package com.puppynoteserver.alertSetting.service.request;

import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertSettingUpdateServiceRequest {

	private final AlertType all;
	private final AlertType smokingWarning;
	private final AlertType leaderboardRank;
	private final AlertType challenge;
	private final AlertType report;

	@Builder
	public AlertSettingUpdateServiceRequest(AlertType all, AlertType smokingWarning,
		AlertType leaderboardRank, AlertType challenge, AlertType report) {
		this.all = all;
		this.smokingWarning = smokingWarning;
		this.leaderboardRank = leaderboardRank;
		this.challenge = challenge;
		this.report = report;
	}
}
