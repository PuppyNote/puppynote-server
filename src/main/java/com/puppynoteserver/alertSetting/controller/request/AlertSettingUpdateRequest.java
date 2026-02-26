package com.puppynoteserver.alertSetting.controller.request;

import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import com.puppynoteserver.alertSetting.service.request.AlertSettingUpdateServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlertSettingUpdateRequest {

	@NotNull(message = "전체 알림 설정은 필수입니다.")
	private AlertType all;

	@NotNull(message = "흡연 주의 알림 설정은 필수입니다.")
	private AlertType smokingWarning;

	@NotNull(message = "리더보드 순위 알림 설정은 필수입니다.")
	private AlertType leaderboardRank;

	@NotNull(message = "챌린지 알림 설정은 필수입니다.")
	private AlertType challenge;

	@NotNull(message = "리포트 알림 설정은 필수입니다.")
	private AlertType report;

	@Builder
	public AlertSettingUpdateRequest(AlertType all, AlertType smokingWarning,
		AlertType leaderboardRank, AlertType challenge, AlertType report) {
		this.all = all;
		this.smokingWarning = smokingWarning;
		this.leaderboardRank = leaderboardRank;
		this.challenge = challenge;
		this.report = report;
	}

	public AlertSettingUpdateServiceRequest toServiceRequest() {
		return AlertSettingUpdateServiceRequest.builder()
			.all(all)
			.smokingWarning(smokingWarning)
			.leaderboardRank(leaderboardRank)
			.challenge(challenge)
			.report(report)
			.build();
	}
}
