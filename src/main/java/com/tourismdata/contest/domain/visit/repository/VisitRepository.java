package com.tourismdata.contest.domain.visit.repository;

import com.tourismdata.contest.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

// ⚠️ Developer A 담당 도메인. Story Mode 구현에 필요해 최소 JpaRepository 상속만 채움.
// A가 커스텀 쿼리 메서드를 추가할 수 있으니 병합 전 확인 필요.
public interface VisitRepository extends JpaRepository<Visit, Long> {
}
