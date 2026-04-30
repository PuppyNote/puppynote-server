package com.puppynoteserver.docs.weather;

import com.puppynoteserver.docs.RestDocsSupport;
import com.puppynoteserver.weather.controller.WeatherController;
import com.puppynoteserver.weather.service.WeatherService;
import com.puppynoteserver.weather.service.request.WeatherServiceRequest;
import com.puppynoteserver.weather.service.response.WeatherResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WeatherControllerDocsTest extends RestDocsSupport {

    private final WeatherService weatherService = mock(WeatherService.class);

    @Override
    protected Object initController() {
        return new WeatherController(weatherService);
    }

    @DisplayName("날씨 조회 API")
    @Test
    void getWeather() throws Exception {
        // given
        WeatherResponse response = new WeatherResponse(5.2, 0, "맑음", 12.5, 0.0);
        given(weatherService.getWeather(any(WeatherServiceRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/weather")
                        .param("latitude", "37.56")
                        .param("longitude", "126.97"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("weather-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("latitude").description("위도 (소수점 최대 7자리)"),
                                parameterWithName("longitude").description("경도 (소수점 최대 7자리)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.temperature").type(JsonFieldType.NUMBER)
                                        .description("현재 기온 (°C)"),
                                fieldWithPath("data.weatherCode").type(JsonFieldType.NUMBER)
                                        .description("""
                                                WMO 날씨 코드 +
                                                `0` 맑음 +
                                                `1` 대체로 맑음 +
                                                `2` 부분적으로 흐림 +
                                                `3` 흐림 +
                                                `45`, `48` 안개 +
                                                `51`~`55` 이슬비 +
                                                `56`, `57` 어는 이슬비 +
                                                `61`~`65` 비 +
                                                `66`, `67` 어는 비 +
                                                `71`~`75` 눈 +
                                                `77` 눈 결정 +
                                                `80`~`82` 소나기 +
                                                `85`, `86` 눈 소나기 +
                                                `95` 뇌우 +
                                                `96`, `99` 우박 동반 뇌우\
                                                """),
                                fieldWithPath("data.weatherDescription").type(JsonFieldType.STRING)
                                        .description("날씨 설명 (한국어, weatherCode에 대응하는 텍스트)"),
                                fieldWithPath("data.windSpeed").type(JsonFieldType.NUMBER)
                                        .description("풍속 (km/h)"),
                                fieldWithPath("data.precipitation").type(JsonFieldType.NUMBER)
                                        .description("강수량 (mm, 직전 15분 누적)")
                        )
                ));
    }
}
