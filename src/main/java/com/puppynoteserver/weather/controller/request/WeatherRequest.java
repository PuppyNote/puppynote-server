package com.puppynoteserver.weather.controller.request;

import com.puppynoteserver.weather.service.request.WeatherServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherRequest {

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    public WeatherServiceRequest toServiceRequest() {
        return WeatherServiceRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
