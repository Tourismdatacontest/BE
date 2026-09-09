package com.tourismdata.contest.domain.visit.repository;

import com.tourismdata.contest.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

// ⚠️ Developer A 담당 도메인. feat/story 작업 때 이미 이 수정 했었는데, feat/story PR이
// develop에 안 머지돼서 이 브랜치엔 반영이 안 돼있었음. feat/story도 나중에 별도로 머지 필요.
public interface VisitRepository extends JpaRepository<Visit, Long> {
}