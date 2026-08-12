package com.puppynoteserver.global.security.config;

import com.puppynoteserver.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig의 인가 규칙 순서와 CORS 설정을 실제 필터체인 위에서 검증한다.
 * JwtAuthenticationFilter가 getServletPath()로 인증 제외 경로를 판단하는데
 * MockMvc는 servletPath를 자동으로 채워주지 않으므로, 실제 서블릿 컨테이너와 동일하게 servletPath를 지정한다.
 */
@AutoConfigureMockMvc
class SecurityConfigTest extends IntegrationTestSupport {

    // application-test.yml의 cors.allowed-origin-patterns에 등록된 값
    private static final String LOCAL_WEB_ORIGIN = "http://localhost:5173";
    private static final String CONFIGURED_WEB_ORIGIN = "https://front-web.example.com";
    private static final String NOT_ALLOWED_ORIGIN = "https://not-allowed.example.com";

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("permitAll 대상인 회원가입 API는 토큰 없이 호출해도 인증 오류가 발생하지 않는다.")
    @Test
    void 토큰없이_회원가입API를_호출하면_인증오류가_아니라_요청값검증오류가_발생한다() throws Exception {
        // when // then : 요청값이 비어 있어 400이 나온다면 인가 단계는 통과했다는 의미이다.
        mockMvc.perform(
                        post("/api/v1/user/signup")
                                .servletPath("/api/v1/user/signup")
                                .content("{}")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("permitAll 대상인 로그인 API는 토큰 없이 호출해도 인증 오류가 발생하지 않는다.")
    @Test
    void 토큰없이_로그인API를_호출하면_인증오류가_아니라_요청값검증오류가_발생한다() throws Exception {
        // when // then
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .servletPath("/api/v1/auth/login")
                                .content("{}")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("permitAll 대상인 앱 버전 조회 API는 토큰 없이 호출해도 인증 오류가 발생하지 않는다.")
    @Test
    void 토큰없이_앱버전_조회API를_호출하면_200을_반환한다() throws Exception {
        // when // then
        mockMvc.perform(
                        get("/api/v1/app-version")
                                .servletPath("/api/v1/app-version")
                )
                .andExpect(status().isOk());
    }

    @DisplayName("인증이 필요한 API는 토큰 없이 호출하면 401을 반환한다.")
    @Test
    void 토큰없이_인증이필요한API를_호출하면_401을_반환한다() throws Exception {
        // when // then
        mockMvc.perform(
                        get("/api/v1/pets")
                                .header(HttpHeaders.ORIGIN, LOCAL_WEB_ORIGIN)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("허용된 Origin의 preflight 요청은 토큰 없이도 통과한다.")
    @Test
    void 허용된Origin의_preflight요청은_토큰없이도_통과한다() throws Exception {
        // when // then
        mockMvc.perform(
                        options("/api/v1/pets")
                                .header(HttpHeaders.ORIGIN, LOCAL_WEB_ORIGIN)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, HttpHeaders.AUTHORIZATION)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, LOCAL_WEB_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @DisplayName("설정 파일에 등록한 Origin의 preflight 요청은 통과한다.")
    @Test
    void 설정파일에_등록한Origin의_preflight요청은_통과한다() throws Exception {
        // when // then
        mockMvc.perform(
                        options("/api/v1/pets")
                                .header(HttpHeaders.ORIGIN, CONFIGURED_WEB_ORIGIN)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, CONFIGURED_WEB_ORIGIN));
    }

    @DisplayName("허용되지 않은 Origin의 preflight 요청은 차단된다.")
    @Test
    void 허용되지않은Origin의_preflight요청은_차단된다() throws Exception {
        // when // then
        mockMvc.perform(
                        options("/api/v1/pets")
                                .header(HttpHeaders.ORIGIN, NOT_ALLOWED_ORIGIN)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                )
                .andExpect(status().isForbidden());
    }

}
