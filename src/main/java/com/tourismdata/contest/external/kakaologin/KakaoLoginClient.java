package com.tourismdata.contest.external.kakaologin;

import com.tourismdata.contest.external.kakaologin.dto.KakaoTokenResponse;
import com.tourismdata.contest.external.kakaologin.dto.KakaoUserProfileResponse;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 카카오 로그인(Authorization Code 방식) 연동 클라이언트.
 *
 * ⚠️ 프론트가 인가 코드(code)를 받아 백엔드로 넘긴다는 가정으로 작성함 - 아직 프론트와
 * 확정된 방식이 아님(2026-09 기준). 만약 프론트가 카카오 JS SDK로 access token을 직접
 * 받아서 넘기는 방식으로 정해지면, exchangeCodeForToken() 호출을 생략하고
 * fetchProfile()만 그 토큰으로 바로 호출하도록 AuthService를 수정하면 됨.
 *
 * ⚠️ RestTemplate과 JDK HttpClient 둘 다 카카오 서버에서 KOE001(Not Acceptable, 토큰
 * 교환) 또는 401(프로필 조회)로 거부당하는 문제가 있었음(원인 미상 - TLS 핑거프린트
 * 차단으로 추정, curl은 매번 정상 동작). 그래서 curl 서브프로세스로 우회함. 프로덕션
 * 배포 전 근본 원인 조사 필요 - curl 바이너리 없는 환경(minimal 컨테이너 등)에선 동작
 * 안 함.
 *
 * ⚠️⚠️ 심각한 버그였음(2026-09-20 발견/수정): 카카오가 에러 응답
 * ({"error":"invalid_grant",...} 등)을 돌려줘도, 그 JSON이 KakaoTokenResponse/
 * KakaoUserProfileResponse의 필드명과 안 겹치면 Jackson이 예외를 던지지 않고 모든
 * 필드가 null인 객체로 "파싱 성공" 처리해버림. 이 null accessToken/id가 그대로
 * AuthService까지 흘러가면 kakaoSubject가 String.valueOf(null) == "null" 문자열로
 * 고정되어, 가짜 code로도 누구나 로그인(또는 그 "null" 계정 재사용)이 가능했음.
 * 그래서 파싱 성공 여부와 별개로 핵심 필드(accessToken, id) null 여부를 직접 검증함.
 */
@Component
public class KakaoLoginClient {

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginClient.class);

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    private final JsonMapper objectMapper = JsonMapper.builder().build();
    private final KakaoLoginProperties properties;

    public KakaoLoginClient(KakaoLoginProperties properties) {
        this.properties = properties;
    }

    public KakaoTokenResponse exchangeCodeForToken(String code) {
        String output = runCurl(new ProcessBuilder(
                "curl", "-s", TOKEN_URL,
                "-d", "grant_type=authorization_code",
                "-d", "client_id=" + properties.clientId(),
                "-d", "client_secret=" + properties.clientSecret(),
                "-d", "redirect_uri=" + properties.redirectUri(),
                "-d", "code=" + code
        ), "카카오 토큰 교환");

        KakaoTokenResponse response;
        try {
            response = objectMapper.readValue(output, KakaoTokenResponse.class);
        } catch (Exception e) {
            log.error("카카오 토큰 교환 응답 파싱 실패. raw response={}", output, e);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }

        if (response.accessToken() == null || response.accessToken().isBlank()) {
            log.error("카카오 토큰 교환 실패로 판단(access_token 없음, 잘못된 code로 추정). raw response={}", output);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }

        return response;
    }

    public KakaoUserProfileResponse fetchProfile(String kakaoAccessToken) {
        String output = runCurl(new ProcessBuilder(
                "curl", "-s", PROFILE_URL,
                "-H", "Authorization: Bearer " + kakaoAccessToken
        ), "카카오 프로필 조회");

        KakaoUserProfileResponse response;
        try {
            response = objectMapper.readValue(output, KakaoUserProfileResponse.class);
        } catch (Exception e) {
            log.error("카카오 프로필 조회 응답 파싱 실패. raw response={}", output, e);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }

        if (response.id() == null) {
            log.error("카카오 프로필 조회 실패로 판단(id 없음). raw response={}", output);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }

        return response;
    }

    private String runCurl(ProcessBuilder pb, String actionLabel) {
        try {
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String errorOutput = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();

            log.info("{} curl 실행 완료. exitCode={} stdout={} stderr={}",
                    actionLabel, exitCode, output, errorOutput);

            if (exitCode != 0) {
                throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
            }

            return output;
        } catch (IOException | InterruptedException e) {
            log.error("{} curl 프로세스 실행 자체 실패", actionLabel, e);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }
    }
}
