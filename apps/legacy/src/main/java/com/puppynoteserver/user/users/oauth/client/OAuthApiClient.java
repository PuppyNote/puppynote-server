package com.puppynoteserver.user.users.oauth.client;

import com.puppynoteserver.user.users.entity.enums.SnsType;

public interface OAuthApiClient {
    SnsType oAuthSnsType();
    String getEmail(String code);
}
