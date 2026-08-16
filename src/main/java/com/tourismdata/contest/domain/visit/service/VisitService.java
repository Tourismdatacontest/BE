package com.tourismdata.contest.domain.visit.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.mode.repository.ModeRepository;
import com.tourismdata.contest.domain.visit.dto.CollectedIngredientResponse;
import com.tourismdata.contest.domain.visit.dto.VisitCreateRequest;
import com.tourismdata.contest.domain.visit.dto.VisitResponse;
import com.tourismdata.contest.domain.visit.dto.VisitResultResponse;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;
import com.tourismdata.contest.domain.visit.repository.VisitLocationLogRepository;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;
import com.tourismdata.contest.global.util.GeoUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final VisitLocationLogRepository visitLocationLogRepository;
    private final CourseRepository courseRepository;
    private final ModeRepository modeRepository;

    public VisitResponse createVisit(VisitCreateRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VISIT_REQUEST));
        Mode mode = modeRepository.findById(request.modeId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VISIT_REQUEST));

        Visit visit = Visit.builder()
                .course(course)
                .mode(mode)
                .build();
        visitRepository.save(visit);

        return VisitResponse.from(visit);
    }

    @Transactional(readOnly = true)
    public VisitResponse getVisit(Long visitId) {
        return VisitResponse.from(findVisitOrThrow(visitId));
    }

    public VisitResponse completeVisit(Long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        String summary = "%s 코스 탐방 완료 (체크포인트 %d개 방문)"
                .formatted(visit.getCourse().getTitle(), visit.getVisitedCheckpointCount());
        visit.complete(summary);
        return VisitResponse.from(visit);
    }

    @Transactional(readOnly = true)
    public VisitResultResponse getVisitResult(Long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        List<VisitLocationLog> logs = visitLocationLogRepository.findByVisit_VisitIdOrderByRecordedAtAsc(visitId);

        return new VisitResultResponse(
                visit.getVisitId(),
                visit.getCourse().getCourseId(),
                calculateTotalDistanceM(logs),
                calculateDurationSeconds(visit),
                visit.getVisitedCheckpointCount(),
                List.<CollectedIngredientResponse>of(), // TODO: Story 도메인 완성되면 실제 수집 재료로 교체
                visit.getResultSummary()
        );
    }

    private Visit findVisitOrThrow(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new CustomException(ErrorCode.VISIT_NOT_FOUND));
    }

    private static int calculateTotalDistanceM(List<VisitLocationLog> logs) {
        double total = 0;
        for (int i = 1; i < logs.size(); i++) {
            VisitLocationLog prev = logs.get(i - 1);
            VisitLocationLog curr = logs.get(i);
            total += GeoUtils.distanceInMeters(
                    prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude());
        }
        return (int) Math.round(total);
    }

    private static Integer calculateDurationSeconds(Visit visit) {
        if (visit.getCompletedAt() == null) {
            return null;
        }
        return (int) Duration.between(visit.getStartedAt(), visit.getCompletedAt()).getSeconds();
    }
}
