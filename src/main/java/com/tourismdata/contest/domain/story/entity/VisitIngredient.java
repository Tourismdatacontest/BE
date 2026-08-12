package com.tourismdata.contest.domain.story.entity;

import com.tourismdata.contest.domain.visit.entity.Visit;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ERD 복합키(visit_id, ingredient_id) 반영 - @EmbeddedId 사용
@Entity
@Table(name = "visit_ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitIngredient {

    @EmbeddedId
    private VisitIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("visit")
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredient")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @Builder
    public VisitIngredient(Visit visit, Ingredient ingredient) {
        this.visit = visit;
        this.ingredient = ingredient;
        this.id = new VisitIngredientId(visit.getVisitId(), ingredient.getIngredientId());
        this.acquiredAt = LocalDateTime.now();
    }
}
