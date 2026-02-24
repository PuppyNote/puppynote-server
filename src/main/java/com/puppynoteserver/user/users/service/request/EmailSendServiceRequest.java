package com.puppynoteserver.user.users.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailSendServiceRequest {
    private final String email;

    @Builder
    private EmailSendServiceRequest(String email) {
        this.email = email;
    }
}
