package com.puppynoteserver.weather.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponse {

    private Current current;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {

        @JsonProperty("temperature_2m")
        private double temperature;

        @JsonProperty("weathercode")
        private int weatherCode;

        @JsonProperty("windspeed_10m")
        private double windSpeed;

        @JsonProperty("precipitation")
        private double precipitation;
    }
}
