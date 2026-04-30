package com.puppynoteserver.weather.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.redis.service.RedisService;
import com.puppynoteserver.weather.feign.OpenMeteoFeignClient;
import com.puppynoteserver.weather.feign.response.OpenMeteoResponse;
import com.puppynoteserver.weather.service.WeatherService;
import com.puppynoteserver.weather.service.request.WeatherServiceRequest;
import com.puppynoteserver.weather.service.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private static final String CURRENT_FIELDS = "temperature_2m,weathercode,windspeed_10m,precipitation";
    private static final String TIMEZONE = "Asia/Seoul";
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final String CACHE_KEY_PREFIX = "weather:";

    private final OpenMeteoFeignClient openMeteoFeignClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Override
    public WeatherResponse getWeather(WeatherServiceRequest request) {
        String cacheKey = buildCacheKey(request.getLatitude(), request.getLongitude());

        String cached = redisService.getValue(cacheKey);
        if (cached != null) {
            return deserialize(cached);
        }

        OpenMeteoResponse apiResponse = openMeteoFeignClient.getForecast(
                request.getLatitude(), request.getLongitude(), CURRENT_FIELDS, TIMEZONE);
        WeatherResponse response = WeatherResponse.of(apiResponse);

        redisService.setValue(cacheKey, serialize(response), CACHE_TTL);
        return response;
    }

    // 0.05도 그리드로 반올림하여 ~5km 반경 내 동일 캐시 키 사용
    private String buildCacheKey(double latitude, double longitude) {
        double gridLat = Math.round(latitude / 0.05) * 0.05;
        double gridLon = Math.round(longitude / 0.05) * 0.05;
        return CACHE_KEY_PREFIX + gridLat + ":" + gridLon;
    }

    private String serialize(WeatherResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("날씨 응답 직렬화 실패", e);
            throw new RuntimeException("날씨 데이터 처리 중 오류가 발생했습니다.");
        }
    }

    private WeatherResponse deserialize(String json) {
        try {
            return objectMapper.readValue(json, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            log.error("날씨 캐시 역직렬화 실패", e);
            throw new RuntimeException("날씨 데이터 처리 중 오류가 발생했습니다.");
        }
    }
}
