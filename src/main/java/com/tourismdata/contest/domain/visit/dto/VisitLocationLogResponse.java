package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;

public record VisitLocationLogResponse(
        Long logId,
        UUID visitId,
        Double latitude,
        Double longitude,
        LocalDateTime recordedAt,
        Boolean synced
) {

    public static VisitLocationLogResponse from(VisitLocationLog log) {
        return new VisitLocationLogResponse(
                log.getLogId(),
                log.getVisit().getVisitUuid(),
                log.getLatitude(),
                log.getLongitude(),
                log.getRecordedAt(),
                log.getSynced()
        );
    }
}
