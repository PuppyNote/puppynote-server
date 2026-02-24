package com.puppynoteserver.healthRecord.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.healthRecord.entity.enums.RecordType;
import com.puppynoteserver.pet.pets.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "health_records")
public class HealthRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RecordType recordType;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false, length = 255)
    private String value;

    @Column(length = 20)
    private String unit;

    public static HealthRecord of(Pet pet, RecordType recordType, LocalDate recordDate, String value, String unit) {
        HealthRecord record = new HealthRecord();
        record.pet = pet;
        record.recordType = recordType;
        record.recordDate = recordDate;
        record.value = value;
        record.unit = unit;
        return record;
    }
}
