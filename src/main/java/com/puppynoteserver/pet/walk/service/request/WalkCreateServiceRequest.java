package com.puppynoteserver.pet.walk.service.request;

import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.walk.entity.Walk;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalkCreateServiceRequest {

    private final Long petId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String location;
    private final String memo;
    private final List<String> photoKeys;

    @Builder
    private WalkCreateServiceRequest(Long petId, LocalDateTime startTime, LocalDateTime endTime,
                                     BigDecimal latitude, BigDecimal longitude,
                                     String location, String memo, List<String> photoKeys) {
        this.petId = petId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.memo = memo;
        this.photoKeys = photoKeys;
    }

    public Long getPetId() {
        return petId;
    }

    public List<String> getPhotoKeys() {
        return photoKeys;
    }

    public Walk toEntity(Pet pet) {
        return Walk.of(pet, startTime, endTime, latitude, longitude, location, memo);
    }
}
