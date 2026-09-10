package com.tourismdata.contest.domain.visit.dto;

import java.time.LocalDateTime;

import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;

// reachedCheckpoint: 이번 위치 갱신으로 새 체크포인트에 도달했을 때만 채워짐(그 외엔 null).
// 프론트는 이 값이 null이 아니면 checkpointId로 Story 도메인의
// GET /visits/{visitId}/story/checkpoints/{checkpointId}를 호출해 스토리 이벤트를 이어서 진행하면 된다.
public record VisitLocationLogResponse(
        Long logId,
        Long visitId,
        Double latitude,
        Double longitude,
        LocalDateTime recordedAt,
        Boolean synced,
        NearbyCheckpointResponse reachedCheckpoint
) {

    // Offline-Sync(Developer C) 등 체크포인트 판정이 필요 없는 호출부를 위한 하위 호환 오버로드.
    public static VisitLocationLogResponse from(VisitLocationLog log) {
        return from(log, null);
    }

    public static VisitLocationLogResponse from(VisitLocationLog log, NearbyCheckpointResponse reachedCheckpoint) {
        return new VisitLocationLogResponse(
                log.getLogId(),
                log.getVisit().getVisitId(),
                log.getLatitude(),
                log.getLongitude(),
                log.getRecordedAt(),
                log.getSynced(),
                reachedCheckpoint
        );
    }
}
