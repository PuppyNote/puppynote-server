package com.puppynoteserver.pet.petWalkAlarms.repository;

import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface PetWalkAlarmJpaRepository extends JpaRepository<PetWalkAlarm, Long> {

    List<PetWalkAlarm> findByPetId(Long petId);

    @Query("SELECT DISTINCT a FROM PetWalkAlarm a JOIN FETCH a.pet WHERE a.alarmStatus = :status AND a.alarmTime = :time AND :day MEMBER OF a.alarmDays")
    List<PetWalkAlarm> findActiveAlarmsAtTimeAndDay(@Param("status") AlarmStatus status, @Param("time") LocalTime time, @Param("day") AlarmDay day);

    @Query("SELECT a FROM PetWalkAlarm a WHERE a.pet.id = :petId AND a.alarmStatus = :status AND :day MEMBER OF a.alarmDays ORDER BY a.alarmTime ASC")
    List<PetWalkAlarm> findTodayAlarmsByPetId(@Param("petId") Long petId, @Param("status") AlarmStatus status, @Param("day") AlarmDay day);
}
