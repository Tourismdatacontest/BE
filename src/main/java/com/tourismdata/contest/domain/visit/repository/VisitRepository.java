package com.tourismdata.contest.domain.visit.repository;

import com.tourismdata.contest.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

// ⚠️ Developer A 담당 도메인. Story Mode(정현)와 Kakao 로그인(정현) 양쪽에서 각각
// 최소 JpaRepository 상속만 채워뒀음. A가 커스텀 쿼리 메서드 추가할 수 있으니 확인 필요.
public interface VisitRepository extends JpaRepository<Visit, Long> {
}