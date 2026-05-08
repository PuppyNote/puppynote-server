package com.puppynoteserver.weather.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.weather.controller.request.WeatherRequest;
import com.puppynoteserver.weather.service.WeatherService;
import com.puppynoteserver.weather.service.response.WeatherResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ApiResponse<WeatherResponse> getWeather(@ModelAttribute @Valid WeatherRequest request) {
        return ApiResponse.ok(weatherService.getWeather(request.toServiceRequest()));
    }
}
