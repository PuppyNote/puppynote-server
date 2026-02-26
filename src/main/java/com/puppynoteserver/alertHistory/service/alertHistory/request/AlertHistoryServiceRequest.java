package com.puppynoteserver.alertHistory.service.alertHistory.request;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.alertHistory.entity.AlertHistory;
import com.puppynoteserver.alertHistory.entity.AlertHistoryStatus;
import com.puppynoteserver.firebase.request.SendFirebaseServiceRequest;
import com.puppynoteserver.user.users.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlertHistoryServiceRequest {

	private User user;
	private String alertDescription;
	private AlertDestinationType alertDestinationType;
	private String alertDestinationInfo;

	@Builder
	private AlertHistoryServiceRequest(User user, String alertDescription, AlertDestinationType alertDestinationType, String alertDestinationInfo) {
		this.user = user;
		this.alertDescription = alertDescription;
		this.alertDestinationType = alertDestinationType;
		this.alertDestinationInfo = alertDestinationInfo;
	}

	public AlertHistory toEntity() {
		return AlertHistory.builder()
			.user(user)
			.alertDescription(alertDescription)
			.alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
			.alertDestinationType(alertDestinationType)
			.alertDestinationInfo(alertDestinationInfo)
			.build();
	}

	public static AlertHistoryServiceRequest of(SendFirebaseServiceRequest sendPushServiceRequest) {
		return AlertHistoryServiceRequest.builder()
			.user(sendPushServiceRequest.getPush().getUser())
			.alertDescription(sendPushServiceRequest.getBody())
			.alertDestinationType(sendPushServiceRequest.getSendFirebaseDataDto().getAlert_destination_type())
			.alertDestinationInfo(sendPushServiceRequest.getSendFirebaseDataDto().getAlert_destination_info())
			.build();
	}
}
