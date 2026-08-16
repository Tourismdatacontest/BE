package com.tourismdata.contest.domain.course.dto;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.entity.Difficulty;

public record CourseSummaryResponse(
        Long courseId,
        String title,
        String thumbnailUrl,
        Integer distanceM,
        Integer estimatedMinutes,
        Difficulty difficulty
) {

    public static CourseSummaryResponse from(Course course) {
        return new CourseSummaryResponse(
                course.getCourseId(),
                course.getTitle(),
                course.getThumbnailUrl(),
                course.getDistanceM(),
                course.getEstimatedMinutes(),
                course.getDifficulty()
        );
    }
}
