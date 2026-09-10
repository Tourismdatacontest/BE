package com.tourismdata.contest.domain.visit.dto;

// Story 도메인의 Ingredient를 VisitResultResponse.collectedIngredients 응답 형태로 옮긴 것.
public record CollectedIngredientResponse(
        Long ingredientId,
        String name,
        String imageUrl
) {
}
