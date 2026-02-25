package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetWalkAlarmJpaRepository extends JpaRepository<PetWalkAlarm, Long> {

    List<PetWalkAlarm> findByPetId(Long petId);
}
