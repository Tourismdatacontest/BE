package com.tourismdata.contest.domain.course.dto;

import java.time.LocalDateTime;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.entity.Difficulty;

public record CourseDetailResponse(
        Long courseId,
        String title,
        String thumbnailUrl,
        Integer distanceM,
        Integer estimatedMinutes,
        Difficulty difficulty,
        String description,
        LocalDateTime createdAt
) {

    public static CourseDetailResponse from(Course course) {
        return new CourseDetailResponse(
                course.getCourseId(),
                course.getTitle(),
                course.getThumbnailUrl(),
                course.getDistanceM(),
                course.getEstimatedMinutes(),
                course.getDifficulty(),
                course.getDescription(),
                course.getCreatedAt()
        );
    }
}
