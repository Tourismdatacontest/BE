package com.tourismdata.contest.domain.visit.dto;

public record VisitCreateRequest(
        Long courseId,
        Long modeId
) {
}
