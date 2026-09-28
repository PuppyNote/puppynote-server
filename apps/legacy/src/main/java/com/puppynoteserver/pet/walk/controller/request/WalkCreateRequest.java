package com.puppynoteserver.pet.walk.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.puppynoteserver.pet.walk.service.request.WalkCreateServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class WalkCreateRequest {

    @NotNull(message = "펫 ID는 필수입니다.")
    private Long petId;

    @NotNull(message = "산책 시작 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "산책 종료 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "위도는 필수입니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    private BigDecimal longitude;

    private String location;

    private String memo;

    // Storage API로 사전 업로드 후 받은 S3 Object Key 목록
    private List<String> photoKeys;

    @Builder
    private WalkCreateRequest(Long petId, LocalDateTime startTime, LocalDateTime endTime,
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

    public WalkCreateServiceRequest toServiceRequest() {
        return WalkCreateServiceRequest.builder()
                .petId(petId)
                .startTime(startTime)
                .endTime(endTime)
                .latitude(latitude)
                .longitude(longitude)
                .location(location)
                .memo(memo)
                .photoKeys(photoKeys)
                .build();
    }
}
