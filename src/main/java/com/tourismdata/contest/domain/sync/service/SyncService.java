package com.tourismdata.contest.domain.sync.service;

import com.tourismdata.contest.domain.sync.dto.LocationBulkSyncRequest;
import com.tourismdata.contest.domain.visit.dto.LocationUpdateRequest;
import com.tourismdata.contest.domain.visit.dto.VisitLocationLogResponse;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;
import com.tourismdata.contest.domain.visit.repository.VisitLocationLogRepository;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 오프라인 중 쌓인 위치 로그 일괄 동기화.
 * 재연결 시 클라이언트가 로컬에 쌓아둔 위치 기록을 한 번에 전송하면, 서버는 각 기록을
 * synced=true 상태로 저장한다(지금 이 순간 서버와 동기화됐다는 의미).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SyncService {

    private final VisitRepository visitRepository;
    private final VisitLocationLogRepository visitLocationLogRepository;

    public List<VisitLocationLogResponse> syncLocations(Long visitId, LocationBulkSyncRequest request) {
        Visit visit = visitRepository.findById(visitId)
            .orElseThrow(() -> new IllegalArgumentException("탐방 세션을 찾을 수 없습니다. visitId=" + visitId));

        List<VisitLocationLog> logs = request.locations().stream()
            .map(loc -> toLog(visit, loc))
            .toList();

        List<VisitLocationLog> saved = visitLocationLogRepository.saveAll(logs);

        return saved.stream()
            .map(VisitLocationLogResponse::from)
            .toList();
    }

    private VisitLocationLog toLog(Visit visit, LocationUpdateRequest loc) {
        LocalDateTime recordedAt = (loc.recordedAt() != null) ? loc.recordedAt() : LocalDateTime.now();
        VisitLocationLog log = VisitLocationLog.builder()
            .visit(visit)
            .latitude(loc.latitude())
            .longitude(loc.longitude())
            .recordedAt(recordedAt)
            .build();
        log.markSynced();
        return log;
    }
}
