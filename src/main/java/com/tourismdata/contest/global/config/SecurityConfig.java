package com.tourismdata.contest.global.config;

import com.tourismdata.contest.global.security.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 하이브리드 로그인(팀 결정, 2026-09 업데이트): 기본은 비로그인 게스트 진행, 스토리 모드
// 보상 시점에 선택적으로 카카오 로그인. 로그인 필수 보호 엔드포인트는 아직 없어서 전체
// permitAll 유지하되, /admin/**(TourAPI 연동, 코스/스토리 시딩 등 내부 운영 도구)는
// local 프로필에서만 열어둔다.
//
// ⚠️ 운영 DB에 시딩 작업이 필요할 때(배포 초기 등)를 위해 ADMIN_API_ENABLED=true
// 환경변수로 임시 개방 가능하게 함. 기본값은 false(차단) - 작업 끝나면 반드시 다시
// false로 돌리거나 변수를 지울 것.
//
// ⚠️ CORS: 프론트가 백엔드와 다른 오리진(Cloudflare Workers/Pages 등)에서 브라우저로
// 직접 호출하기 때문에 명시적 설정 필요. workers.dev/pages.dev 서브도메인 전체와
// localhost 아무 포트나 패턴으로 허용해둠 - 넓은 설정이라, 프론트 도메인이 커스텀
// 도메인으로 확정되면 정확한 origin으로 좁히는 걸 권장.
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.workers.dev",
                "https://*.pages.dev",
                "http://localhost:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Profile("local")
    public SecurityFilterChain localFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Profile("!local")
    public SecurityFilterChain defaultFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            @Value("${admin.api.enabled:false}") boolean adminApiEnabled
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> {
                    if (!adminApiEnabled) {
                        auth.requestMatchers("/admin/**").denyAll();
                    }
                    auth.anyRequest().permitAll();
                });
        return http.build();
    }
}
