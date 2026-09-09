package com.tourismdata.contest.domain.story.dto;

import com.tourismdata.contest.domain.story.entity.VisitIngredient;

import java.time.LocalDateTime;

public record VisitIngredientResponse(
    Long visitId,
    Long ingredientId,
    LocalDateTime acquiredAt
) {
    public static VisitIngredientResponse from(VisitIngredient entity) {
        return new VisitIngredientResponse(
            entity.getVisit().getVisitId(),
            entity.getIngredient().getIngredientId(),
            entity.getAcquiredAt()
        );
    }
}
