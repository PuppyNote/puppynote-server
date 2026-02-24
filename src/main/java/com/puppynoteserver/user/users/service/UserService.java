package com.puppynoteserver.user.users.service;

import com.puppynoteserver.user.users.service.request.EmailSendServiceRequest;
import com.puppynoteserver.user.users.service.request.EmailVerifyServiceRequest;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;

public interface UserService {
    SignUpResponse signUp(SignUpServiceRequest request);
    String sendVerificationEmail(EmailSendServiceRequest request);
}
