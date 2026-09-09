package com.tourismdata.contest.domain.course.repository;

import com.tourismdata.contest.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

// ⚠️ Developer A 담당 도메인. Offline Sync 구현에 필요해 최소 JpaRepository 상속만 채움.
public interface CourseRepository extends JpaRepository<Course, Long> {
}
