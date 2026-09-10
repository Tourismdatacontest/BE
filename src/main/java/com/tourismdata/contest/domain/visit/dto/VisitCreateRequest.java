package com.tourismdata.contest.domain.visit.dto;

import jakarta.validation.constraints.NotNull;

public record VisitCreateRequest(
        @NotNull Long courseId,
        @NotNull Long modeId
) {
}
