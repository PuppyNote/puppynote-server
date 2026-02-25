package com.puppynoteserver.pet.petWalkAlarms.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmWriteService;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetWalkAlarmWriteServiceImpl implements PetWalkAlarmWriteService {

    private final PetWalkAlarmRepository petWalkAlarmRepository;
    private final PetReadService petReadService;

    @Override
    public PetWalkAlarmResponse create(PetWalkAlarmCreateServiceRequest request) {
        Pet pet = petReadService.findById(request.getPetId());
        PetWalkAlarm savedAlarm = petWalkAlarmRepository.save(request.toEntity(pet));
        return PetWalkAlarmResponse.from(savedAlarm);
    }

    @Override
    public PetWalkAlarmResponse update(PetWalkAlarmUpdateServiceRequest request) {
        PetWalkAlarm petWalkAlarm = petWalkAlarmRepository.findById(request.getAlarmId())
                .orElseThrow(() -> new NotFoundException("알람을 찾을 수 없습니다."));
        request.applyTo(petWalkAlarm);
        return PetWalkAlarmResponse.from(petWalkAlarm);
    }

    @Override
    public PetWalkAlarmResponse updateStatus(PetWalkAlarmStatusUpdateServiceRequest request) {
        PetWalkAlarm petWalkAlarm = petWalkAlarmRepository.findById(request.getAlarmId())
                .orElseThrow(() -> new NotFoundException("알람을 찾을 수 없습니다."));
        request.applyTo(petWalkAlarm);
        return PetWalkAlarmResponse.from(petWalkAlarm);
    }

    @Override
    public void delete(Long alarmId) {
        PetWalkAlarm petWalkAlarm = petWalkAlarmRepository.findById(alarmId)
                .orElseThrow(() -> new NotFoundException("알람을 찾을 수 없습니다."));
        petWalkAlarmRepository.delete(petWalkAlarm);
    }
}
