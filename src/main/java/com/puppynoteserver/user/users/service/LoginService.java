package com.puppynoteserver.user.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.user.users.service.request.LoginServiceRequest;
import com.puppynoteserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppynoteserver.user.users.service.request.TokenRefreshServiceRequest;
import com.puppynoteserver.user.users.service.response.LoginResponse;
import com.puppynoteserver.user.users.service.response.OAuthLoginResponse;
import com.puppynoteserver.user.users.service.response.TokenRefreshResponse;

public interface LoginService {

    LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException;

    OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest oAuthLoginServiceRequest) throws
            JsonProcessingException;

    TokenRefreshResponse refresh(TokenRefreshServiceRequest request);

}
