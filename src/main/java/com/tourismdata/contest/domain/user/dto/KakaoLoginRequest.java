package com.tourismdata.contest.domain.user.dto;

// visitId는 선택 - 스토리 모드 보상 획득 시점에 로그인하는 경우 그 방문 세션을 연결하기 위함.
// 단순 로그인만 하는 경우(마이페이지 진입 등)엔 null.
public record KakaoLoginRequest(
    String code,
    Long visitId
) {
}
