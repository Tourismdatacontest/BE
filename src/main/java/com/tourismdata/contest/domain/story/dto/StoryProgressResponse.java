package com.tourismdata.contest.domain.story.dto;

public record StoryProgressResponse(
    Long visitId,
    Long currentCheckpointId,
    Integer totalCheckpoints,
    Integer completedCheckpoints,
    Integer collectedIngredientCount
) {
}
