package com.puppynoteserver.pet.petWalkAlarms.service.impl;

import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetWalkAlarmReadServiceImpl implements PetWalkAlarmReadService {

    private final PetWalkAlarmRepository petWalkAlarmRepository;

    @Override
    public List<PetWalkAlarmResponse> getAlarmsByPetId(Long petId) {
        return petWalkAlarmRepository.findByPetId(petId).stream()
                .map(PetWalkAlarmResponse::from)
                .toList();
    }
}
