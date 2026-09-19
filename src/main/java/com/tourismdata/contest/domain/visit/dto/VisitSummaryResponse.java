package com.tourismdata.contest.domain.visit.dto;

import com.tourismdata.contest.domain.visit.entity.Visit;

import java.time.LocalDateTime;

public record VisitSummaryResponse(
    Long visitId,
    Long courseId,
    String courseTitle,
    String courseThumbnailUrl,
    Long modeId,
    String status,
    Integer visitedCheckpointCount,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {
    public static VisitSummaryResponse from(Visit visit) {
        return new VisitSummaryResponse(
            visit.getVisitId(),
            visit.getCourse().getCourseId(),
            visit.getCourse().getTitle(),
            visit.getCourse().getThumbnailUrl(),
            visit.getMode().getModeId(),
            visit.getStatus().name(),
            visit.getVisitedCheckpointCount(),
            visit.getStartedAt(),
            visit.getCompletedAt()
        );
    }
}
