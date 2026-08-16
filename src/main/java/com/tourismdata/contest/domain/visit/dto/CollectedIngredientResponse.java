package com.tourismdata.contest.domain.visit.dto;

// Story 도메인(재료 수집)이 완성되면 실제 데이터로 채워질 자리.
// 지금은 VisitResultResponse.collectedIngredients의 응답 형태만 명세에 맞춰둔 placeholder라
// 항상 빈 리스트로 반환된다.
public record CollectedIngredientResponse(
        Long ingredientId,
        String name,
        String imageUrl
) {
}
