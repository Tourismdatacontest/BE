package com.tourismdata.contest.domain.story.dto;

import com.tourismdata.contest.domain.story.entity.StoryEvent;

public record StoryEventResponse(
    Long storyEventId,
    Long checkpointId,
    String content,
    IngredientResponse ingredient
) {
    public static StoryEventResponse from(StoryEvent entity) {
        IngredientResponse ingredientResponse = entity.getIngredient() != null
            ? IngredientResponse.from(entity.getIngredient())
            : null;

        return new StoryEventResponse(
            entity.getStoryEventId(),
            entity.getCheckpoint().getCheckpointId(),
            entity.getContent(),
            ingredientResponse
        );
    }
}
