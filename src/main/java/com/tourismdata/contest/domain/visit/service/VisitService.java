package com.tourismdata.contest.domain.visit.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.mode.repository.ModeRepository;
import com.tourismdata.contest.domain.story.entity.VisitIngredient;
import com.tourismdata.contest.domain.story.repository.VisitIngredientRepository;
import com.tourismdata.contest.domain.user.repository.UserRepository;
import com.tourismdata.contest.domain.visit.dto.CollectedIngredientResponse;
import com.tourismdata.contest.domain.visit.dto.VisitCreateRequest;
import com.tourismdata.contest.domain.visit.dto.VisitLocationLogResponse;
import com.tourismdata.contest.domain.visit.dto.VisitResponse;
import com.tourismdata.contest.domain.visit.dto.VisitResultResponse;
import com.tourismdata.contest.domain.visit.dto.VisitSummaryResponse;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.entity.VisitLocationLog;
import com.tourismdata.contest.domain.visit.entity.VisitStatus;
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
    private final VisitIngredientRepository visitIngredientRepository;
    private final CourseRepository courseRepository;
    private final ModeRepository modeRepository;
    private final UserRepository userRepository;

    // userId는 Authorization 헤더(Bearer 토큰)로 로그인 여부를 판단한 결과(컨트롤러에서 파싱해 전달).
    // null이면 게스트로 시작 - 이 경우 스토리 모드 보상 시점에 로그인하면 AuthService.linkVisits()가
    // Visit.linkUser()로 나중에 연결한다. 이미 로그인한 상태로 새 탐방을 시작하면 그 자리에서 바로
    // 계정에 연결해, 마이페이지 기록에 즉시 남도록 한다(로그인 상태에서 시작한 탐방이 연결이 안
    // 되는 문제 - 프론트 전달, 안혜선).
    public VisitResponse createVisit(VisitCreateRequest request, Long userId) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VISIT_REQUEST));
        Mode mode = modeRepository.findById(request.modeId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VISIT_REQUEST));

        Visit visit = Visit.builder()
                .course(course)
                .mode(mode)
                .build();

        if (userId != null) {
            // 토큰은 유효했지만(JwtProvider 검증 통과) 가리키는 유저가 이미 탈퇴 등으로 없는
            // 경우에도 탐방 생성 자체는 막지 않고 게스트로 진행한다.
            userRepository.findById(userId).ifPresent(visit::linkUser);
        }

        visitRepository.save(visit);

        return VisitResponse.from(visit);
    }

    @Transactional(readOnly = true)
    public VisitResponse getVisit(Long visitId) {
        return VisitResponse.from(findVisitOrThrow(visitId));
    }

    /** 로그인한 유저의 탐방 기록 목록 - 마이페이지 등에서 사용. 최신 시작 순 정렬. */
    @Transactional(readOnly = true)
    public List<VisitSummaryResponse> getVisitsByUser(Long userId) {
        return visitRepository.findByUser_UserIdOrderByStartedAtDesc(userId).stream()
                .map(VisitSummaryResponse::from)
                .toList();
    }

    public VisitResponse completeVisit(Long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        if (visit.getStatus() == VisitStatus.COMPLETED) {
            // 이미 완료된 탐방을 다시 완료 처리하면 completedAt이 덮어써져 소요시간이 틀어지므로,
            // 재요청은 상태를 바꾸지 않고 현재 상태 그대로 반환한다.
            return VisitResponse.from(visit);
        }
        String summary = "%s 코스 탐방 완료 (체크포인트 %d개 방문)"
                .formatted(visit.getCourse().getTitle(), visit.getVisitedCheckpointCount());
        visit.complete(summary);
        return VisitResponse.from(visit);
    }

    // 마이페이지 "다시보기" - 탐방 중 기록된 좌표를 시간순 그대로 반환해 프론트가 지도에 경로로 그림.
    @Transactional(readOnly = true)
    public List<VisitLocationLogResponse> getLocationLogs(Long visitId) {
        findVisitOrThrow(visitId);
        return visitLocationLogRepository.findByVisit_VisitIdOrderByRecordedAtAscLogIdAsc(visitId).stream()
                .map(VisitLocationLogResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VisitResultResponse getVisitResult(Long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        List<VisitLocationLog> logs = visitLocationLogRepository
                .findByVisit_VisitIdOrderByRecordedAtAscLogIdAsc(visitId);

        List<CollectedIngredientResponse> collectedIngredients = visitIngredientRepository
                .findByVisit_VisitId(visitId).stream()
                .map(VisitService::toCollectedIngredientResponse)
                .toList();

        return new VisitResultResponse(
                visit.getVisitId(),
                visit.getCourse().getCourseId(),
                calculateTotalDistanceM(logs),
                calculateDurationSeconds(visit),
                visit.getVisitedCheckpointCount(),
                collectedIngredients,
                visit.getResultSummary()
        );
    }

    private static CollectedIngredientResponse toCollectedIngredientResponse(VisitIngredient visitIngredient) {
        return new CollectedIngredientResponse(
                visitIngredient.getIngredient().getIngredientId(),
                visitIngredient.getIngredient().getName(),
                visitIngredient.getIngredient().getImageUrl()
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
