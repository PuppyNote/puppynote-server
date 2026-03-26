package com.puppynoteserver.user.users.oauth.client.apple.dto;

import lombok.Getter;

@Getter
public class AppleIdTokenPayload {
    private String sub;

    private String email;
}
