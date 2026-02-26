package com.puppynoteserver.alertSetting.service.response;

import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertSettingResponse {

	private final AlertType all;
	private final AlertType smokingWarning;
	private final AlertType leaderboardRank;
	private final AlertType challenge;
	private final AlertType report;

	@Builder
	private AlertSettingResponse(AlertType all, AlertType smokingWarning,
		AlertType leaderboardRank, AlertType challenge, AlertType report) {
		this.all = all;
		this.smokingWarning = smokingWarning;
		this.leaderboardRank = leaderboardRank;
		this.challenge = challenge;
		this.report = report;
	}

	public static AlertSettingResponse createResponse(AlertSetting alertSetting) {
		return AlertSettingResponse.builder()
			.all(alertSetting.getAll())
			.smokingWarning(alertSetting.getSmokingWarning())
			.leaderboardRank(alertSetting.getLeaderboardRank())
			.challenge(alertSetting.getChallenge())
			.report(alertSetting.getReport())
			.build();
	}
}
