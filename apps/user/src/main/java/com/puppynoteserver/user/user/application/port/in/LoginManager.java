package com.puppynoteserver.user.user.application.port.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.user.user.application.port.in.request.*;
import com.puppynoteserver.user.user.application.port.in.response.LoginResponse;
import com.puppynoteserver.user.user.application.port.in.response.OAuthLoginResponse;

public interface LoginManager {

    LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException;

    OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest oAuthLoginServiceRequest) throws
            JsonProcessingException;

    void resetPassword(PasswordResetServiceRequest request);

    String sendPasswordResetEmail(EmailSendServiceRequest request);

}
