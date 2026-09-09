package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;

public record VisitLocationLogResponse(
        Long logId,
        Long visitId,
        Double latitude,
        Double longitude,
        LocalDateTime recordedAt,
        Boolean synced
) {

    public static VisitLocationLogResponse from(VisitLocationLog log) {
        return new VisitLocationLogResponse(
                log.getLogId(),
                log.getVisit().getVisitId(),
                log.getLatitude(),
                log.getLongitude(),
                log.getRecordedAt(),
                log.getSynced()
        );
    }
}
