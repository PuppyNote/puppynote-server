package com.puppynoteserver.user.user.application.port.in;

import com.puppynoteserver.user.user.application.port.in.request.SignUpServiceRequest;
import com.puppynoteserver.user.user.application.port.in.response.SignUpResponse;

public interface UserRegister {
    SignUpResponse signUp(SignUpServiceRequest request);
}
