package com.tourismdata.contest.domain.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;

public interface VisitLocationLogRepository extends JpaRepository<VisitLocationLog, Long> {

    List<VisitLocationLog> findByVisit_VisitIdOrderByRecordedAtAsc(Long visitId);
}
