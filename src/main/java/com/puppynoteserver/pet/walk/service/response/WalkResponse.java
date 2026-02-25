package com.puppynoteserver.pet.walk.service.response;

import com.puppynoteserver.pet.walk.entity.Walk;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class WalkResponse {

    private final Long walkId;
    private final Long petId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String location;
    private final String memo;
    private final List<String> photoUrls;

    private WalkResponse(Long walkId, Long petId, LocalDateTime startTime, LocalDateTime endTime,
                         BigDecimal latitude, BigDecimal longitude,
                         String location, String memo, List<String> photoUrls) {
        this.walkId = walkId;
        this.petId = petId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.memo = memo;
        this.photoUrls = photoUrls;
    }

    public static WalkResponse of(Walk walk, List<String> photoUrls) {
        return new WalkResponse(
                walk.getId(),
                walk.getPet().getId(),
                walk.getStartTime(),
                walk.getEndTime(),
                walk.getLatitude(),
                walk.getLongitude(),
                walk.getLocation(),
                walk.getMemo(),
                photoUrls
        );
    }
}
