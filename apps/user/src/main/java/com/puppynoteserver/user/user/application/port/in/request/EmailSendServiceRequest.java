package com.puppynoteserver.user.user.application.port.in.request;

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
