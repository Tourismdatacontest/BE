package com.tourismdata.contest.global.config;

import com.tourismdata.contest.global.security.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 하이브리드 로그인(팀 결정, 2026-09 업데이트):
 * 기본은 비로그인 게스트 진행, 스토리 모드 보상 시점에 선택적으로 카카오 로그인.
 * 로그인 필수 보호 엔드포인트는 아직 없어서 전체 permitAll 유지하되,
 * /admin/**(TourAPI 연동, 코스/스토리 시딩 등 내부 운영 도구)는 local 프로필에서만 허용.
 *
 * ⚠️ 운영 DB 시딩 작업(배포 초기 등)을 위해 ADMIN_API_ENABLED=true 환경변수로 임시 개방 가능.
 * 기본값은 false(차단)이며, 작업 완료 후 반드시 false로 전환하거나 환경변수를 제거할 것.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "https://namsanddu.anhs-0218.workers.dev",
            "http://localhost:3000",
            "http://localhost:5173"
    );

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Environment env,
            @Value("${admin.api.enabled:false}") boolean adminApiEnabled
    ) throws Exception {
        boolean isLocal = env.acceptsProfiles(Profiles.of("local"));
        boolean allowAdminAccess = isLocal || adminApiEnabled;

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    if (!allowAdminAccess) {
                        auth.requestMatchers("/admin/**").denyAll();
                    }
                    auth.anyRequest().permitAll();
                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(ALLOWED_ORIGINS);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
