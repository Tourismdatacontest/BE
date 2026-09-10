package com.tourismdata.contest.global.config;

import com.tourismdata.contest.global.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 하이브리드 로그인(팀 결정, 2026-09 업데이트): 기본은 비로그인 게스트 진행, 스토리 모드
// 보상 시점에 선택적으로 카카오 로그인. 로그인 필수 보호 엔드포인트는 아직 없어서 전체
// permitAll 유지하되, /admin/**(TourAPI 연동 등 내부 운영 도구)는 local 프로필에서만 열어둔다.
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
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").denyAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
