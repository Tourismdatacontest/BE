package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// recordedAt은 선택 - 클라이언트가 오프라인 중 기록한 실제 시각을 보낼 수도 있고,
// 없으면 서버에서 동기화 시점(now)으로 채움.
public record LocationUpdateRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        LocalDateTime recordedAt
) {
}
