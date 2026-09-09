package com.tourismdata.contest.external.kakaologin;

import com.tourismdata.contest.external.kakaologin.dto.KakaoTokenResponse;
import com.tourismdata.contest.external.kakaologin.dto.KakaoUserProfileResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * 카카오 로그인(Authorization Code 방식) 연동 클라이언트.
 *
 * ⚠️ 프론트가 인가 코드(code)를 받아 백엔드로 넘긴다는 가정으로 작성함 - 아직 프론트와
 * 확정된 방식이 아님(2026-09 기준). 만약 프론트가 카카오 JS SDK로 access token을 직접
 * 받아서 넘기는 방식으로 정해지면, exchangeCodeForToken() 호출을 생략하고
 * fetchProfile()만 그 토큰으로 바로 호출하도록 AuthService를 수정하면 됨.
 *
 * ⚠️ 토큰 교환은 RestTemplate이 아니라 JDK HttpClient를 직접 사용함 - RestTemplate으로
 * 보내면 파라미터/헤더가 다 맞아도 카카오 서버에서 KOE001(Not Acceptable)로 거부하는
 * 문제가 있었고, curl과 JDK HttpClient는 정상 동작 확인함. 원인은 명확히 못 밝혔지만
 * (RestTemplate 내부 HTTP 구현체의 저수준 차이로 추정), 일단 동작하는 방식으로 우회함.
 */
@Component
public class KakaoLoginClient {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final JsonMapper objectMapper = JsonMapper.builder().build();
    private final KakaoLoginProperties properties;

    public KakaoLoginClient(KakaoLoginProperties properties) {
        this.properties = properties;
    }

    public KakaoTokenResponse exchangeCodeForToken(String code) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", TOKEN_URL,
                    "-d", "grant_type=authorization_code",
                    "-d", "client_id=" + properties.clientId(),
                    "-d", "client_secret=" + properties.clientSecret(),
                    "-d", "redirect_uri=" + properties.redirectUri(),
                    "-d", "code=" + code
            );
            Process process = pb.start();

            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorOutput = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                throw new IllegalStateException("curl 실행 실패. exitCode=" + exitCode + " stderr=" + errorOutput);
            }

            return objectMapper.readValue(output, KakaoTokenResponse.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("카카오 토큰 교환 요청 실패(curl subprocess)", e);
        }
    }

    public KakaoUserProfileResponse fetchProfile(String kakaoAccessToken) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", PROFILE_URL,
                    "-H", "Authorization: Bearer " + kakaoAccessToken
            );
            Process process = pb.start();

            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorOutput = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                throw new IllegalStateException("curl 실행 실패. exitCode=" + exitCode + " stderr=" + errorOutput);
            }

            return objectMapper.readValue(output, KakaoUserProfileResponse.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("카카오 프로필 조회 요청 실패(curl subprocess)", e);
        }
    }

    private String send(HttpRequest request, String actionLabel) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                    actionLabel + " 실패. status=" + response.statusCode() + " body=" + response.body());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(actionLabel + " 요청 실패", e);
        }
    }
}
