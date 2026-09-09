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

import java.util.List;

/**
 * 카카오 로그인 서비스.
 * ⚠️ Authorization Code 방식 가정 - 프론트 확정 방식 아님(KakaoLoginClient 주석 참고).
 *
 * visitIds: 게스트 방문 세션을 로그인 계정에 연결. 몇 개를 보낼지(로그인 시점 방문 하나만 /
 * 게스트 시절 전체 복구)는 프론트/기획 결정 사항이라 백엔드는 리스트로 유연하게 받음.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final KakaoLoginClient kakaoLoginClient;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final JwtProvider jwtProvider;

    public KakaoLoginResponse loginWithKakao(String code, List<Long> visitIds) {
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

        linkVisits(user, visitIds);

        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        return new KakaoLoginResponse(accessToken, user.getUserId(), user.getNickname());
    }

    private void linkVisits(User user, List<Long> visitIds) {
        if (visitIds == null) {
            return;
        }
        for (Long visitId : visitIds) {
            Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "탐방 세션을 찾을 수 없습니다. visitId=" + visitId));
            visit.linkUser(user);
        }
    }
}
