package com.puppynoteserver.pet.walk.service;

import com.puppynoteserver.pet.walk.service.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;

public interface WalkWriteService {

    WalkResponse create(WalkCreateServiceRequest request);
}
