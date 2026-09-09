package com.tourismdata.contest.domain.user.dto;

public record KakaoLoginResponse(
    String accessToken,
    Long userId,
    String nickname
) {
}
