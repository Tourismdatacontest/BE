package com.tourismdata.contest.domain.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.course.entity.Checkpoint;

public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    List<Checkpoint> findByCourse_CourseIdOrderByOrderNoAsc(Long courseId);
}
