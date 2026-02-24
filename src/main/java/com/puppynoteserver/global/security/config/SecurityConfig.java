package com.puppynoteserver.global.security.config;

import com.puppynoteserver.jwt.JwtTokenProvider;
import com.puppynoteserver.jwt.filter.JwtAuthenticationFilter;
import com.puppynoteserver.jwt.filter.JwtExceptionHandlerFilter;
import com.puppynoteserver.user.users.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	CorsConfigurationSource corsConfigurationSource() {
		return request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedHeaders(Collections.singletonList("*"));
			config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOrigins(Arrays.asList(
                    "https://www.quitmate.co.kr",
                    "https://quitmate.co.kr",
                    "http://localhost:8081"
            ));
			config.setAllowCredentials(true);
			return config;
		};
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))

			.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

			.authorizeHttpRequests(auth -> auth
				.requestMatchers(getPublicMatchers()).permitAll()
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
		return new RequestMatcher[] {
			new AntPathRequestMatcher("/api/v1/jwt/**"),
			new AntPathRequestMatcher("/api/v1/auth/**"),
			new AntPathRequestMatcher("/api/v1/user/**"),
			new AntPathRequestMatcher("/docs/**"),
			new AntPathRequestMatcher("/health-check")
		};
	}
}

