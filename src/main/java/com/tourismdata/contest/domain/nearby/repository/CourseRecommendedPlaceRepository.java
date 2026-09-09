package com.tourismdata.contest.domain.nearby.repository;

import com.tourismdata.contest.domain.nearby.entity.CourseRecommendedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRecommendedPlaceRepository extends JpaRepository<CourseRecommendedPlace, Long> {

    // Course 엔티티 PK 필드명이 courseId라는 가정 - 다르면 메서드명 수정 필요
    List<CourseRecommendedPlace> findByCourse_CourseIdOrderByOrderNoAsc(Long courseId);
}
