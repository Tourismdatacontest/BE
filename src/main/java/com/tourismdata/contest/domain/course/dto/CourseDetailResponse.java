package com.tourismdata.contest.domain.course.dto;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.entity.Difficulty;

import java.time.LocalDateTime;

public record CourseDetailResponse(
    Long courseId,
    String title,
    String description,
    String thumbnailUrl,
    Integer distanceM,
    Integer estimatedMinutes,
    Difficulty difficulty,
    LocalDateTime createdAt
) {
    public static CourseDetailResponse from(Course entity) {
        return new CourseDetailResponse(
            entity.getCourseId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getThumbnailUrl(),
            entity.getDistanceM(),
            entity.getEstimatedMinutes(),
            entity.getDifficulty(),
            entity.getCreatedAt()
        );
    }
}
