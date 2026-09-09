package com.tourismdata.contest.domain.story.repository;

import com.tourismdata.contest.domain.story.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
