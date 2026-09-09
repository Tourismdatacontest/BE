package com.tourismdata.contest.domain.story.controller;

import com.tourismdata.contest.domain.story.dto.IngredientAcquireRequest;
import com.tourismdata.contest.domain.story.dto.StoryEventResponse;
import com.tourismdata.contest.domain.story.dto.StoryIntroResponse;
import com.tourismdata.contest.domain.story.dto.StoryProgressResponse;
import com.tourismdata.contest.domain.story.dto.VisitIngredientResponse;
import com.tourismdata.contest.domain.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping("/visits/{visitId}/story/intro")
    public StoryIntroResponse getStoryIntro(@PathVariable Long visitId) {
        return storyService.getStoryIntro(visitId);
    }

    @GetMapping("/visits/{visitId}/story/checkpoints/{checkpointId}")
    public StoryEventResponse getStoryEvent(
        @PathVariable Long visitId,
        @PathVariable Long checkpointId
    ) {
        return storyService.getStoryEvent(visitId, checkpointId);
    }

    @PostMapping("/visits/{visitId}/story/ingredients")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitIngredientResponse acquireIngredient(
        @PathVariable Long visitId,
        @RequestBody IngredientAcquireRequest request
    ) {
        return storyService.acquireIngredient(visitId, request.ingredientId());
    }

    @GetMapping("/visits/{visitId}/story/progress")
    public StoryProgressResponse getStoryProgress(@PathVariable Long visitId) {
        return storyService.getStoryProgress(visitId);
    }
}
