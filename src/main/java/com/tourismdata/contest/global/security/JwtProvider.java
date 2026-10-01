package com.tourismdata.contest.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * JWT 발급/검증.
 * ⚠️ HS256은 최소 32바이트(256비트) 이상 시크릿 필요 - jwt.secret이 짧으면 기동 시 예외 발생.
 * 로컬 테스트용: openssl rand -base64 32 로 생성해서 application-local.yml/환경변수에 설정.
 */
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpireSeconds;

    public JwtProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireSeconds = jwtProperties.accessTokenExpireSeconds();
    }

    public String generateAccessToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessTokenExpireSeconds)))
            .signWith(key)
            .compact();
    }

    public Long parseUserId(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    /**
     * HTTP Authorization 헤더("Bearer {accessToken}")에서 userId를 꺼낸다.
     * 로그인은 선택 사항(하이브리드 로그인)인 엔드포인트에서 쓰는 용도라, 헤더가 없거나
     * 토큰이 없거나/만료됐거나/형식이 잘못돼도 예외를 던지지 않고 그냥 게스트로 취급한다.
     */
    public Optional<Long> parseUserIdFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        try {
            return Optional.of(parseUserId(authorizationHeader.substring("Bearer ".length())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
