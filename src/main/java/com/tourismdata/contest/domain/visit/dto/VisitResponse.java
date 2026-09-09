package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.entity.VisitStatus;

public record VisitResponse(
        Long visitId,
        Long courseId,
        Long modeId,
        Long currentCheckpointId,
        Integer visitedCheckpointCount,
        VisitStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {

    public static VisitResponse from(Visit visit) {
        return new VisitResponse(
                visit.getVisitId(),
                visit.getCourse().getCourseId(),
                visit.getMode().getModeId(),
                visit.getCurrentCheckpoint() != null ? visit.getCurrentCheckpoint().getCheckpointId() : null,
                visit.getVisitedCheckpointCount(),
                visit.getStatus(),
                visit.getStartedAt(),
                visit.getCompletedAt()
        );
    }
}
