package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

// recordedAt은 선택 - 클라이언트가 오프라인 중 기록한 실제 시각을 보낼 수도 있고,
// 없으면 서버에서 동기화 시점(now)으로 채움.
public record LocationUpdateRequest(
    Double latitude,
    Double longitude,
    LocalDateTime recordedAt
) {
}
