package com.puppynoteserver.weather.feign;

import com.puppynoteserver.weather.feign.response.OpenMeteoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openMeteoApi", url = "${openmeteo.api.url}")
public interface OpenMeteoFeignClient {

    @GetMapping("/v1/forecast")
    OpenMeteoResponse getForecast(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("current") String current,
            @RequestParam("timezone") String timezone
    );
}
