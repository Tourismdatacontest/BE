package com.tourismdata.contest.domain.visit.dto;

import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;

import java.time.LocalDateTime;

public record VisitLocationLogResponse(
    Long logId,
    Long visitId,
    Double latitude,
    Double longitude,
    LocalDateTime recordedAt,
    Boolean synced
) {
    public static VisitLocationLogResponse from(VisitLocationLog entity) {
        return new VisitLocationLogResponse(
            entity.getLogId(),
            entity.getVisit().getVisitId(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getRecordedAt(),
            entity.getSynced()
        );
    }
}
