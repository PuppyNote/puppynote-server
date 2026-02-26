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

	@NotNull(message = "산책 알림 설정은 필수입니다.")
	private AlertType walk;

	@NotNull(message = "친구 알림 설정은 필수입니다.")
	private AlertType friend;

	@Builder
	public AlertSettingUpdateRequest(AlertType all, AlertType walk, AlertType friend) {
		this.all = all;
		this.walk = walk;
		this.friend = friend;
	}

	public AlertSettingUpdateServiceRequest toServiceRequest() {
		return AlertSettingUpdateServiceRequest.builder()
			.all(all)
			.walk(walk)
			.friend(friend)
			.build();
	}
}
