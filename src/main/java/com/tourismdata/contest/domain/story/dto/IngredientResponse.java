package com.tourismdata.contest.domain.story.dto;

import com.tourismdata.contest.domain.story.entity.Ingredient;
import lombok.Getter;

@Getter
public class IngredientResponse {

    private final Long ingredientId;
    private final String name;
    private final String imageUrl;

    private IngredientResponse(Ingredient entity) {
        this.ingredientId = entity.getIngredientId();
        this.name = entity.getName();
        this.imageUrl = entity.getImageUrl();
    }

    public static IngredientResponse from(Ingredient entity) {
        return new IngredientResponse(entity);
    }
}
