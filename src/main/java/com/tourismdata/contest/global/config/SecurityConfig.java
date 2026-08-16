package com.tourismdata.contest.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// TODO: 인증 방식 확정 후 구현 (기기 간 기록 공유 필요 여부에 따라 결정)
// 비로그인 방식 확정 전까지는 전체 요청을 허용해 기본 Basic Auth 잠금을 해제한다.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
