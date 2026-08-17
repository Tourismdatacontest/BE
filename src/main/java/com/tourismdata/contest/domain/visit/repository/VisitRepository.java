package com.tourismdata.contest.domain.visit.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.visit.entity.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByVisitUuid(UUID visitUuid);
}
