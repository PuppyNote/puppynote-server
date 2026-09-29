package com.puppynoteserver.pet.walk.application.port.in;

import com.puppynoteserver.pet.walk.application.port.in.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkResponse;

public interface WalkRegister {

    WalkResponse create(WalkCreateServiceRequest request);
}
