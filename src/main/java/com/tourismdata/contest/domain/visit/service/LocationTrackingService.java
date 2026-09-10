package com.tourismdata.contest.domain.visit.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.visit.dto.LocationUpdateRequest;
import com.tourismdata.contest.domain.visit.dto.NearbyCheckpointResponse;
import com.tourismdata.contest.domain.visit.dto.VisitLocationLogResponse;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;
import com.tourismdata.contest.domain.visit.repository.VisitLocationLogRepository;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;
import com.tourismdata.contest.global.util.GeoUtils;

import lombok.RequiredArgsConstructor;

// ST_DWithin 대신 GeoUtils(Haversine)로 nearbyCheckpoint 판정.
// Story(Developer B) 연동 지점: recordLocation()의 응답(VisitLocationLogResponse.reachedCheckpoint)이
// null이 아니면 그 checkpointId로 GET /visits/{visitId}/story/checkpoints/{checkpointId}를 호출하는
// 구조로 확정됨 (Story 쪽이 checkpointId를 파라미터로 직접 받는 REST 오케스트레이션 방식으로 구현했기 때문).
@Service
@RequiredArgsConstructor
@Transactional
public class LocationTrackingService {

    private static final double CHECKPOINT_TRIGGER_RADIUS_METERS = 150;

    private final VisitRepository visitRepository;
    private final VisitLocationLogRepository visitLocationLogRepository;
    private final CheckpointRepository checkpointRepository;

    public VisitLocationLogResponse recordLocation(Long visitId, LocationUpdateRequest request) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new CustomException(ErrorCode.VISIT_NOT_FOUND));

        LocalDateTime recordedAt = request.recordedAt() != null ? request.recordedAt() : LocalDateTime.now();
        VisitLocationLog log = VisitLocationLog.builder()
                .visit(visit)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .recordedAt(recordedAt)
                .build();
        visitLocationLogRepository.save(log);

        Optional<Checkpoint> reachedCheckpoint = findNextNearbyCheckpoint(visit, request.latitude(), request.longitude());
        reachedCheckpoint.ifPresent(visit::moveTo);

        NearbyCheckpointResponse reachedCheckpointResponse = reachedCheckpoint
                .map(checkpoint -> NearbyCheckpointResponse.of(checkpoint,
                        GeoUtils.distanceInMeters(request.latitude(), request.longitude(),
                                checkpoint.getLatitude(), checkpoint.getLongitude())))
                .orElse(null);

        return VisitLocationLogResponse.from(log, reachedCheckpointResponse);
    }

    @Transactional(readOnly = true)
    public CheckpointResponse getCheckpointDetail(Long checkpointId) {
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHECKPOINT_NOT_FOUND));
        return CheckpointResponse.from(checkpoint);
    }

    // 바로 다음 순번 체크포인트인지부터 확인하고, 그다음에 반경 안인지 판정한다.
    // (순번 이상인 체크포인트 중 아무거나 반경 안에 있으면 걸리도록 짜면, 2번을 건너뛰고
    // 3번 체크포인트 근처에 있다는 이유만으로 진행이 앞당겨지는 버그가 생긴다.)
    private Optional<Checkpoint> findNextNearbyCheckpoint(Visit visit, double latitude, double longitude) {
        int nextOrderNo = visit.getCurrentCheckpoint() != null
                ? visit.getCurrentCheckpoint().getOrderNo() + 1
                : 1;

        return checkpointRepository
                .findByCourse_CourseIdOrderByOrderNoAsc(visit.getCourse().getCourseId())
                .stream()
                .filter(checkpoint -> checkpoint.getOrderNo().equals(nextOrderNo))
                .findFirst()
                .filter(checkpoint -> GeoUtils.isWithinRadius(latitude, longitude,
                        checkpoint.getLatitude(), checkpoint.getLongitude(), CHECKPOINT_TRIGGER_RADIUS_METERS));
    }
}
