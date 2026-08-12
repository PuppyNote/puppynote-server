package com.puppynoteserver.global.security.config;

import com.puppynoteserver.jwt.JwtTokenProvider;
import com.puppynoteserver.jwt.filter.JwtAuthenticationFilter;
import com.puppynoteserver.jwt.filter.JwtExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsProperties corsProperties;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            // 허용 Origin은 profile별 yml(cors.allowed-origin-patterns)에서 관리한다.
            config.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
            config.setAllowCredentials(true);
            config.setMaxAge(corsProperties.getMaxAge());
            return config;
        };
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))

                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                // 규칙은 선언 순서대로 평가되고 먼저 매칭된 규칙이 적용되므로,
                // permitAll 대상은 반드시 authenticated() 규칙보다 위에 선언해야 한다.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/*.env"), new AntPathRequestMatcher("/**/.env")).denyAll()
                        // 브라우저는 preflight(OPTIONS)에 Authorization 헤더를 싣지 않으므로 인증 대상에서 제외한다.
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/about").permitAll()
                        .requestMatchers(getPublicMatchers()).permitAll()
                        .requestMatchers("/api/**", "/auth/**").authenticated()
                        .anyRequest().hasAnyAuthority("USER", "ADMIN")
                )

                .logout((logout) -> logout
                        .logoutSuccessUrl("/"))

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(),
                        JwtAuthenticationFilter.class); //JwtAuthenticationFilter에서 뱉은 에러를 처리하기 위한 Filter

        return http.build();
    }

    private RequestMatcher[] getPublicMatchers() {
        return new RequestMatcher[]{
                new AntPathRequestMatcher("/api/v1/jwt/**"),
                new AntPathRequestMatcher("/api/v1/auth/**"),
                new AntPathRequestMatcher("/api/v1/user/**"),
                new AntPathRequestMatcher("/api/v1/app-version"),
                new AntPathRequestMatcher("/docs/**"),
                new AntPathRequestMatcher("/health-check"),
                new AntPathRequestMatcher("/actuator/**"),
                new AntPathRequestMatcher("/api/v1/test/**"),
                new AntPathRequestMatcher("/test/**")
        };
    }
}