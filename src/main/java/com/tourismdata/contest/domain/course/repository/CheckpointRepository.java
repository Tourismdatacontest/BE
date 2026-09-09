package com.tourismdata.contest.domain.course.repository;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ⚠️ Developer A 담당 도메인. Story Mode 진행 상태 조회(코스 전체 체크포인트 수 계산),
// Offline Sync(코스별 순서대로 조회)에 필요해 최소 추가. 병합 전 A 확인 필요.
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    long countByCourse_CourseId(Long courseId);

    List<Checkpoint> findByCourse_CourseIdOrderByOrderNoAsc(Long courseId);
}
