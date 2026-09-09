package com.tourismdata.contest.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

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
}
