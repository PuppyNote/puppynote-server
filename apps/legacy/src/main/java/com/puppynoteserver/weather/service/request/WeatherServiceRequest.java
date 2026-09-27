package com.puppynoteserver.weather.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherServiceRequest {

    private final double latitude;
    private final double longitude;
}
