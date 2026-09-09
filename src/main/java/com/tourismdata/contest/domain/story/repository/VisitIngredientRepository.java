package com.tourismdata.contest.domain.story.repository;

import com.tourismdata.contest.domain.story.entity.VisitIngredient;
import com.tourismdata.contest.domain.story.entity.VisitIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitIngredientRepository extends JpaRepository<VisitIngredient, VisitIngredientId> {

    List<VisitIngredient> findByVisit_VisitId(Long visitId);

    long countByVisit_VisitId(Long visitId);
}
