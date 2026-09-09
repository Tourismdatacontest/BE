package com.tourismdata.contest.domain.user.dto;

import java.util.List;

// visitIds는 선택 - 스토리 모드 보상 획득 시점에 로그인하는 경우 그 방문 세션(들)을
// 연결하기 위함. 로그인 시점 방문 하나만 보내도 되고(리스트에 1개), 나중에 프론트가
// 게스트 시절 방문 기록을 여러 개 한 번에 복구시키는 방식으로 바뀌어도 백엔드 코드
// 변경 없이 대응 가능. 단순 로그인만 하는 경우(마이페이지 진입 등)엔 null 또는 빈 리스트.
public record KakaoLoginRequest(
    String code,
    List<Long> visitIds
) {
}
