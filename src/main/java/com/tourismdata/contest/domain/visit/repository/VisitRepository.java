package com.tourismdata.contest.domain.visit.repository;

import com.tourismdata.contest.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ⚠️ Developer A 담당 도메인. Story Mode(정현)와 Kakao 로그인(정현) 양쪽에서 각각
// 최소 JpaRepository 상속만 채워뒀음. 유저별 탐방 기록 목록 조회(GET /visits?userId=)에
// findByUser_UserIdOrderByStartedAtDesc 추가. A 확인 필요.
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUser_UserIdOrderByStartedAtDesc(Long userId);
}
