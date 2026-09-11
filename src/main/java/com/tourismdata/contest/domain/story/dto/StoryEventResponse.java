package com.tourismdata.contest.domain.story.dto;

import com.tourismdata.contest.domain.story.entity.StoryEvent;

import java.util.List;

// ⚠️ 스웨거 문서 갱신 필요: ingredient(단일 객체) -> ingredients(배열)로 변경됨.
public record StoryEventResponse(
    Long storyEventId,
    Long checkpointId,
    String content,
    List<IngredientResponse> ingredients
) {
    public static StoryEventResponse from(StoryEvent entity) {
        List<IngredientResponse> ingredientResponses = entity.getIngredients().stream()
            .map(IngredientResponse::from)
            .toList();

        return new StoryEventResponse(
            entity.getStoryEventId(),
            entity.getCheckpoint().getCheckpointId(),
            entity.getContent(),
            ingredientResponses
        );
    }
}
