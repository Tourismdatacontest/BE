package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

public record LocationUpdateRequest(
        Double latitude,
        Double longitude,
        LocalDateTime recordedAt
) {
}
