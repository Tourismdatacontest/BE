package com.tourismdata.contest.domain.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.course.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
