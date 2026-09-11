package com.tourismdata.contest.domain.course.repository;

import com.tourismdata.contest.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// ⚠️ Developer A 담당 도메인. Story 콘텐츠 시딩 시 코스 제목으로 조회하기 위해
// findByTitle 추가.
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitle(String title);
}
