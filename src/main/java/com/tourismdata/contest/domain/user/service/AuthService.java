package com.tourismdata.contest.domain.user.service;

import com.tourismdata.contest.domain.user.dto.KakaoLoginResponse;
import com.tourismdata.contest.domain.user.entity.User;
import com.tourismdata.contest.domain.user.repository.UserRepository;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import com.tourismdata.contest.external.kakaologin.KakaoLoginClient;
import com.tourismdata.contest.external.kakaologin.dto.KakaoTokenResponse;
import com.tourismdata.contest.external.kakaologin.dto.KakaoUserProfileResponse;
import com.tourismdata.contest.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 로그인 서비스.
 * ⚠️ Authorization Code 방식 가정 - 프론트 확정 방식 아님(KakaoLoginClient 주석 참고).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final KakaoLoginClient kakaoLoginClient;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final JwtProvider jwtProvider;

    public KakaoLoginResponse loginWithKakao(String code, Long visitId) {
        KakaoTokenResponse token = kakaoLoginClient.exchangeCodeForToken(code);
        KakaoUserProfileResponse profile = kakaoLoginClient.fetchProfile(token.accessToken());

        String kakaoSubject = String.valueOf(profile.id());
        User user = userRepository.findByKakaoSubject(kakaoSubject)
            .map(existing -> {
                existing.updateProfile(profile.nickname(), profile.profileImageUrl());
                return existing;
            })
            .orElseGet(() -> userRepository.save(
                User.builder()
                    .kakaoSubject(kakaoSubject)
                    .nickname(profile.nickname())
                    .profileImageUrl(profile.profileImageUrl())
                    .build()
            ));

        if (visitId != null) {
            Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "탐방 세션을 찾을 수 없습니다. visitId=" + visitId));
            visit.linkUser(user);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        return new KakaoLoginResponse(accessToken, user.getUserId(), user.getNickname());
    }
}
