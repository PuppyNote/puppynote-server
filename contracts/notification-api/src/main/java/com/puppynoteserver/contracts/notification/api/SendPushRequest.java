package com.puppynoteserver.contracts.notification.api;

public record SendPushRequest(
        Long userId,
        NotificationType type,
        String description,
        String destinationInfo
) {
}
