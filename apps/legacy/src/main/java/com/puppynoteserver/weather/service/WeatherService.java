package com.puppynoteserver.weather.service;

import com.puppynoteserver.weather.service.request.WeatherServiceRequest;
import com.puppynoteserver.weather.service.response.WeatherResponse;

public interface WeatherService {

    WeatherResponse getWeather(WeatherServiceRequest request);
}
