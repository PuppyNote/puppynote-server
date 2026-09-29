package com.puppynoteserver.pet.petWalkAlarms.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmFinder;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmRemover;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmWriter;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.response.PetWalkAlarmResponse;
import com.puppynoteserver.pet.petWalkAlarms.application.port.out.persistence.PetWalkAlarmRepository;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.domain.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.domain.error.PetWalkAlarmErrorMessage;
import com.puppynoteserver.pet.pets.application.port.in.PetFinder;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetWalkAlarmService implements PetWalkAlarmFinder, PetWalkAlarmWriter, PetWalkAlarmRemover {

    private final PetWalkAlarmRepository petWalkAlarmRepository;
    private final PetFinder petFinder;

    @Override
    @Transactional(readOnly = true)
    public PetWalkAlarm findById(Long alarmId) {
        return petWalkAlarmRepository.findById(alarmId)
                .orElseThrow(() -> new NotFoundException(PetWalkAlarmErrorMessage.ALARM_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetWalkAlarmResponse> getAlarmsByPetId(Long petId) {
        return petWalkAlarmRepository.findByPetId(petId).stream()
                .map(PetWalkAlarmResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getTodayAlarmTimes(Long petId) {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        AlarmDay today = AlarmDay.valueOf(dayOfWeek.name().substring(0, 3));
        return petWalkAlarmRepository.findTodayAlarmsByPetId(petId, AlarmStatus.YES, today).stream()
                .map(PetWalkAlarm::getAlarmTime)
                .toList();
    }

    @Override
    public PetWalkAlarmResponse create(PetWalkAlarmCreateServiceRequest request) {
        Pet pet = petFinder.findById(request.getPetId());
        PetWalkAlarm savedAlarm = petWalkAlarmRepository.save(request.toEntity(pet));
        return PetWalkAlarmResponse.from(savedAlarm);
    }

    @Override
    public PetWalkAlarmResponse update(PetWalkAlarmUpdateServiceRequest request) {
        PetWalkAlarm petWalkAlarm = findById(request.getAlarmId());
        request.applyTo(petWalkAlarm);
        return PetWalkAlarmResponse.from(petWalkAlarm);
    }

    @Override
    public PetWalkAlarmResponse updateStatus(PetWalkAlarmStatusUpdateServiceRequest request) {
        PetWalkAlarm petWalkAlarm = findById(request.getAlarmId());
        request.applyTo(petWalkAlarm);
        return PetWalkAlarmResponse.from(petWalkAlarm);
    }

    @Override
    public void delete(Long alarmId) {
        petWalkAlarmRepository.findById(alarmId)
                .ifPresent(petWalkAlarmRepository::delete);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        petWalkAlarmRepository.deleteAllByPetId(petId);
    }
}
