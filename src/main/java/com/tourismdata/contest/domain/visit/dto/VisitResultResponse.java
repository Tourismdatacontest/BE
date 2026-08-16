package com.tourismdata.contest.domain.visit.dto;

import java.util.List;

public record VisitResultResponse(
        Long visitId,
        Long courseId,
        Integer totalDistanceM,
        Integer durationSeconds,
        Integer visitedCheckpointCount,
        List<CollectedIngredientResponse> collectedIngredients,
        String resultSummary
) {
}
