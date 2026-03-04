package com.puppynoteserver.pet.petWalkAlarms.service.impl;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    public List<LocalTime> getTodayAlarmTimes(Long petId) {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        AlarmDay today = AlarmDay.valueOf(dayOfWeek.name().substring(0, 3));
        return petWalkAlarmRepository.findTodayAlarmsByPetId(petId, AlarmStatus.YES, today).stream()
                .map(PetWalkAlarm::getAlarmTime)
                .toList();
    }
}
