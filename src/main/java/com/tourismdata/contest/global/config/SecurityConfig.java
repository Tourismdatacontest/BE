package com.tourismdata.contest.global.config;

import com.tourismdata.contest.global.security.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 하이브리드 로그인(팀 결정, 2026-09 업데이트): 기본은 비로그인 게스트 진행, 스토리 모드
// 보상 시점에 선택적으로 카카오 로그인. 로그인 필수 보호 엔드포인트는 아직 없어서 전체
// permitAll 유지하되, /admin/**(TourAPI 연동, 코스/스토리 시딩 등 내부 운영 도구)는
// local 프로필에서만 열어둔다.
//
// ⚠️ 운영 DB에 시딩 작업이 필요할 때(배포 초기 등)를 위해 ADMIN_API_ENABLED=true
// 환경변수로 임시 개방 가능하게 함. 기본값은 false(차단) - 작업 끝나면 반드시 다시
// false로 돌리거나 변수를 지울 것.
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    @Profile("local")
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
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
            @Value("${admin.api.enabled:false}") boolean adminApiEnabled
    ) throws Exception {
        http
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
