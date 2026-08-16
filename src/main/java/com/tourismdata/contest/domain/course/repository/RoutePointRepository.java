package com.tourismdata.contest.domain.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.course.entity.RoutePoint;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    List<RoutePoint> findByCourse_CourseIdOrderBySequenceAsc(Long courseId);
}
