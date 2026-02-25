package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetWalkAlarmJpaRepository extends JpaRepository<PetWalkAlarm, Long> {
}
