package com.puppynoteserver.pet.walk.service;

import com.puppynoteserver.pet.walk.service.response.WalkResponse;

import java.time.LocalDate;
import java.util.List;

public interface WalkReadService {

    List<WalkResponse> getWalksByPetId(Long petId, LocalDate date);
}
