package com.tourismdata.contest.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// TODO: 인증 방식 확정 후 구현 (기기 간 기록 공유 필요 여부에 따라 결정)
// 비로그인 방식 확정 전까지는 공개 API(명세에 있는 엔드포인트)는 전체 허용해 기본 Basic Auth
// 잠금을 해제하되, /admin/**(TourAPI 연동 등 내부 운영 도구)는 local 프로필에서만 열어둔다.
@Configuration
public class SecurityConfig {

    @Bean
    @Profile("local")
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Profile("!local")
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").denyAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
