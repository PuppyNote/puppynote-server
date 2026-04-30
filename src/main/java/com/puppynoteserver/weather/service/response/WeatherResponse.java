package com.puppynoteserver.weather.service.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.puppynoteserver.weather.feign.response.OpenMeteoResponse;
import lombok.Getter;

@Getter
public class WeatherResponse {

    private final double temperature;
    private final int weatherCode;
    private final String weatherDescription;
    private final double windSpeed;
    private final double precipitation;

    @JsonCreator
    public WeatherResponse(
            @JsonProperty("temperature") double temperature,
            @JsonProperty("weatherCode") int weatherCode,
            @JsonProperty("weatherDescription") String weatherDescription,
            @JsonProperty("windSpeed") double windSpeed,
            @JsonProperty("precipitation") double precipitation) {
        this.temperature = temperature;
        this.weatherCode = weatherCode;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
    }

    public static WeatherResponse of(OpenMeteoResponse response) {
        OpenMeteoResponse.Current current = response.getCurrent();
        return new WeatherResponse(
                current.getTemperature(),
                current.getWeatherCode(),
                resolveDescription(current.getWeatherCode()),
                current.getWindSpeed(),
                current.getPrecipitation()
        );
    }

    private static String resolveDescription(int code) {
        if (code == 0) return "맑음";
        if (code == 1) return "대체로 맑음";
        if (code == 2) return "부분적으로 흐림";
        if (code == 3) return "흐림";
        if (code == 45 || code == 48) return "안개";
        if (code >= 51 && code <= 55) return "이슬비";
        if (code == 56 || code == 57) return "어는 이슬비";
        if (code >= 61 && code <= 65) return "비";
        if (code == 66 || code == 67) return "어는 비";
        if (code >= 71 && code <= 75) return "눈";
        if (code == 77) return "눈 결정";
        if (code >= 80 && code <= 82) return "소나기";
        if (code == 85 || code == 86) return "눈 소나기";
        if (code == 95) return "뇌우";
        if (code == 96 || code == 99) return "우박 동반 뇌우";
        return "알 수 없음";
    }
}
