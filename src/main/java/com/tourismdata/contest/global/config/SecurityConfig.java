package com.tourismdata.contest.global.config;

import com.tourismdata.contest.global.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 하이브리드 로그인(팀 결정, 2026-09 업데이트): 기본은 비로그인 게스트 진행, 스토리 모드
// 보상 시점에 선택적으로 카카오 로그인. 현재 스웨거상 로그인이 필수인 보호 엔드포인트가
// 없어서(로그인은 보상 귀속용) 일단 permitAll 유지 - "내 정보" 등 인증 필요한 API가
// 생기면 JwtProvider로 파싱해 SecurityContext 채우는 필터 추가 필요.
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());
        return http.build();
    }
}
