package com.tourismdata.contest.domain.visit.dto;

import java.util.List;
import java.util.UUID;

public record VisitResultResponse(
        UUID visitId,
        Long courseId,
        Integer totalDistanceM,
        Integer durationSeconds,
        Integer visitedCheckpointCount,
        List<CollectedIngredientResponse> collectedIngredients,
        String resultSummary
) {
}
