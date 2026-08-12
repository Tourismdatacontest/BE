package com.tourismdata.contest.domain.story.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitIngredientId implements Serializable {

    private Long visit;
    private Long ingredient;

    public VisitIngredientId(Long visit, Long ingredient) {
        this.visit = visit;
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitIngredientId that)) return false;
        return Objects.equals(visit, that.visit) && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visit, ingredient);
    }
}
