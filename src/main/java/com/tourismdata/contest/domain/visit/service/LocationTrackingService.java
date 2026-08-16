package com.tourismdata.contest.domain.visit.service;

import java.time.LocalDateTime;
import java.util.List;
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
// Developer B(Story) 연동 지점: checkNearbyCheckpoint()를 그대로 호출해서 이벤트 트리거에 쓰면 된다.
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

        findNextNearbyCheckpoint(visit, request.latitude(), request.longitude())
                .ifPresent(visit::moveTo);

        return VisitLocationLogResponse.from(log);
    }

    @Transactional(readOnly = true)
    public CheckpointResponse getCheckpointDetail(Long checkpointId) {
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHECKPOINT_NOT_FOUND));
        return CheckpointResponse.from(checkpoint);
    }

    @Transactional(readOnly = true)
    public Optional<NearbyCheckpointResponse> checkNearbyCheckpoint(Visit visit, double latitude, double longitude) {
        return findNextNearbyCheckpoint(visit, latitude, longitude)
                .map(checkpoint -> NearbyCheckpointResponse.of(checkpoint,
                        GeoUtils.distanceInMeters(latitude, longitude,
                                checkpoint.getLatitude(), checkpoint.getLongitude())));
    }

    private Optional<Checkpoint> findNextNearbyCheckpoint(Visit visit, double latitude, double longitude) {
        List<Checkpoint> checkpoints = checkpointRepository
                .findByCourse_CourseIdOrderByOrderNoAsc(visit.getCourse().getCourseId());

        int nextOrderNo = visit.getCurrentCheckpoint() != null
                ? visit.getCurrentCheckpoint().getOrderNo() + 1
                : 1;

        return checkpoints.stream()
                .filter(checkpoint -> checkpoint.getOrderNo() >= nextOrderNo)
                .filter(checkpoint -> GeoUtils.isWithinRadius(latitude, longitude,
                        checkpoint.getLatitude(), checkpoint.getLongitude(), CHECKPOINT_TRIGGER_RADIUS_METERS))
                .findFirst();
    }
}
