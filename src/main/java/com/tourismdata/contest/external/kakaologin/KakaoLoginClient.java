package com.tourismdata.contest.external.kakaologin;

import com.tourismdata.contest.external.kakaologin.dto.KakaoTokenResponse;
import com.tourismdata.contest.external.kakaologin.dto.KakaoUserProfileResponse;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;
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
 */
@Component
public class KakaoLoginClient {

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
        return objectMapper.readValue(output, KakaoTokenResponse.class);
    }

    public KakaoUserProfileResponse fetchProfile(String kakaoAccessToken) {
        String output = runCurl(new ProcessBuilder(
                "curl", "-s", PROFILE_URL,
                "-H", "Authorization: Bearer " + kakaoAccessToken
        ), "카카오 프로필 조회");
        return objectMapper.readValue(output, KakaoUserProfileResponse.class);
    }

    private String runCurl(ProcessBuilder pb, String actionLabel) {
        try {
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
            }

            return output;
        } catch (IOException | InterruptedException e) {
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }
    }
}
