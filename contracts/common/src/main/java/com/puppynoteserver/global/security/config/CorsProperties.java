package com.puppynoteserver.global.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS 설정값.
 * 브라우저에서 직접 호출하는 클라이언트(모바일 웹 등)의 Origin은 코드가 아니라
 * profile별 yml의 cors.allowed-origin-patterns 목록에 추가한다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 허용할 Origin 목록.
     * 정확한 Origin(https://www.puppynote.co.kr)과 패턴(http://localhost:[*]) 둘 다 사용할 수 있다.
     * yml에 값이 없으면 로컬 개발 환경만 허용한다.
     */
    private List<String> allowedOriginPatterns = List.of(
            "http://localhost:[*]",
            "http://127.0.0.1:[*]"
    );

    /**
     * preflight(OPTIONS) 응답을 브라우저가 캐시하는 시간(초).
     */
    private long maxAge = 3600L;

}
