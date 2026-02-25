package com.puppynoteserver.pet.petWalkAlarms.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.pets.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_walk_alarms")
public class PetWalkAlarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(length = 3, nullable = false)
    private AlarmStatus alarmStatus;

    @ElementCollection
    @CollectionTable(
            name = "pet_alarm_days",
            joinColumns = @JoinColumn(name = "pet_alarm_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_day", length = 3, nullable = false)
    private Set<AlarmDay> alarmDays = new HashSet<>();

    @Column(nullable = false)
    private LocalTime alarmTime;

    public static PetWalkAlarm of(Pet pet, AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        PetWalkAlarm petAlarm = new PetWalkAlarm();
        petAlarm.pet = pet;
        petAlarm.alarmStatus = alarmStatus;
        petAlarm.alarmDays = alarmDays;
        petAlarm.alarmTime = alarmTime;
        return petAlarm;
    }

    public void update(AlarmStatus alarmStatus, Set<AlarmDay> alarmDays, LocalTime alarmTime) {
        this.alarmStatus = alarmStatus;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
    }
}
