package com.tourismdata.contest.domain.course.repository;

import com.tourismdata.contest.domain.course.entity.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ⚠️ Developer A 담당 도메인. Offline Sync 구현에 필요해 최소 JpaRepository 상속 +
// 코스별 순서(sequence)대로 조회하는 메서드 추가. 병합 전 A 확인 필요.
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    List<RoutePoint> findByCourse_CourseIdOrderBySequenceAsc(Long courseId);
}
