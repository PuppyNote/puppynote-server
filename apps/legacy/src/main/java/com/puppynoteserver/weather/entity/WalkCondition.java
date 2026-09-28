package com.puppynoteserver.weather.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalkCondition {
    GREAT("산책하기 최적인 날씨에요!"),
    GOOD("산책하기 좋은 날씨에요."),
    MODERATE("산책은 가능하지만 주의하세요."),
    BAD("산책을 자제하는 것이 좋아요."),
    DANGER("산책하기 위험한 날씨에요!");

    private final String message;

    public static WalkCondition from(int weatherCode, double temperature) {
        WalkCondition byWeather = fromWeatherCode(weatherCode);
        WalkCondition byTemp = fromTemperature(temperature);
        return byWeather.ordinal() >= byTemp.ordinal() ? byWeather : byTemp;
    }

    public static String resolveMessage(int weatherCode, double temperature) {
        if (temperature >= 32) {
            return String.format("기온이 %.1f°C로 매우 더워요. 뜨거운 아스팔트에 발바닥 화상 위험이 있으니 산책을 자제해주세요!", temperature);
        }
        if (temperature >= 28) {
            return String.format("기온이 %.1f°C로 더워요. 이른 아침이나 저녁에 짧게 산책하고 수분을 충분히 보충해주세요.", temperature);
        }
        if (temperature <= -10) {
            return String.format("기온이 %.1f°C로 매우 추워요. 동상 위험이 있으니 산책을 자제해주세요!", temperature);
        }
        if (temperature <= -5) {
            return String.format("기온이 %.1f°C로 추워요. 방한 용품을 꼭 챙기고 짧게 산책해주세요.", temperature);
        }

        String base = fromWeatherCode(weatherCode).getMessage();

        if (temperature > 0 && temperature <= 5) return base + " 쌀쌀한 날씨니 따뜻하게 입혀주세요.";
        if (temperature >= 23) return base + " 다소 더우니 수분 보충에 신경써주세요.";
        return base;
    }

    private static WalkCondition fromWeatherCode(int code) {
        if (code == 0 || code == 1) return GREAT;
        if (code == 2 || code == 3) return GOOD;
        if (code == 45 || code == 48) return MODERATE;
        if (code >= 51 && code <= 65) return BAD;
        if (code == 66 || code == 67) return DANGER;
        if (code >= 71 && code <= 77) return MODERATE;
        if (code >= 80 && code <= 82) return BAD;
        if (code == 85 || code == 86) return MODERATE;
        if (code == 95 || code == 96 || code == 99) return DANGER;
        return MODERATE;
    }

    private static WalkCondition fromTemperature(double temperature) {
        if (temperature >= 32) return DANGER;
        if (temperature >= 28) return BAD;
        if (temperature >= 23) return MODERATE;
        if (temperature <= -10) return DANGER;
        if (temperature <= -5) return BAD;
        if (temperature <= 0) return MODERATE;
        return GREAT;
    }
}
