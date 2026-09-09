package com.tourismdata.contest.domain.story.service;

import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.story.dto.StoryEventResponse;
import com.tourismdata.contest.domain.story.dto.StoryIntroResponse;
import com.tourismdata.contest.domain.story.dto.StoryProgressResponse;
import com.tourismdata.contest.domain.story.dto.VisitIngredientResponse;
import com.tourismdata.contest.domain.story.entity.Ingredient;
import com.tourismdata.contest.domain.story.entity.StoryEvent;
import com.tourismdata.contest.domain.story.entity.VisitIngredient;
import com.tourismdata.contest.domain.story.entity.VisitIngredientId;
import com.tourismdata.contest.domain.story.repository.IngredientRepository;
import com.tourismdata.contest.domain.story.repository.StoryEventRepository;
import com.tourismdata.contest.domain.story.repository.VisitIngredientRepository;
import com.tourismdata.contest.domain.visit.entity.Visit;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스토리 모드 서비스.
 *
 * ⚠️ checkpointId는 A의 LocationTrackingService(GPS 근접 판정)가 아니라 파라미터로 직접 받는
 * 구조. 실제 GPS 트리거 연동은 A 작업 완료 후 상위(LocationController 등)에서 연결하면 됨.
 * ⚠️ visitId-User 연결(로그인 시점 보상 귀속)은 A의 Kakao OAuth 작업 완료 후 별도 반영 필요.
 * ⚠️ 예외 처리는 우선 IllegalArgumentException으로 처리 - 프로젝트 공통 CustomException/
 * ErrorCode 체계가 갖춰지면 그쪽으로 교체 필요 (global/exception 패키지 참고).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoryService {

    private final VisitRepository visitRepository;
    private final CheckpointRepository checkpointRepository;
    private final StoryEventRepository storyEventRepository;
    private final IngredientRepository ingredientRepository;
    private final VisitIngredientRepository visitIngredientRepository;

    public StoryIntroResponse getStoryIntro(Long visitId) {
        Visit visit = getVisitOrThrow(visitId);
        Course course = visit.getCourse();
        return new StoryIntroResponse(visitId, course.getTitle(), course.getDescription());
    }

    public StoryEventResponse getStoryEvent(Long visitId, Long checkpointId) {
        getVisitOrThrow(visitId); // visit 존재 검증
        StoryEvent event = storyEventRepository.findFirstByCheckpoint_CheckpointId(checkpointId)
            .orElseThrow(() -> new IllegalArgumentException(
                "스토리 이벤트를 찾을 수 없습니다. checkpointId=" + checkpointId));
        return StoryEventResponse.from(event);
    }

    /** 같은 재료를 다시 요청해도 에러 대신 기존 기록을 그대로 반환(멱등 처리). */
    @Transactional
    public VisitIngredientResponse acquireIngredient(Long visitId, Long ingredientId) {
        Visit visit = getVisitOrThrow(visitId);
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new IllegalArgumentException(
                "재료를 찾을 수 없습니다. ingredientId=" + ingredientId));

        VisitIngredientId id = new VisitIngredientId(visitId, ingredientId);
        VisitIngredient visitIngredient = visitIngredientRepository.findById(id)
            .orElseGet(() -> visitIngredientRepository.save(
                VisitIngredient.builder().visit(visit).ingredient(ingredient).build()
            ));

        return VisitIngredientResponse.from(visitIngredient);
    }

    public StoryProgressResponse getStoryProgress(Long visitId) {
        Visit visit = getVisitOrThrow(visitId);
        Course course = visit.getCourse();

        Long currentCheckpointId = (visit.getCurrentCheckpoint() != null)
            ? visit.getCurrentCheckpoint().getCheckpointId()
            : null;

        long totalCheckpoints = checkpointRepository.countByCourse_CourseId(course.getCourseId());
        long collectedIngredientCount = visitIngredientRepository.countByVisit_VisitId(visitId);

        return new StoryProgressResponse(
            visitId,
            currentCheckpointId,
            (int) totalCheckpoints,
            visit.getVisitedCheckpointCount(),
            (int) collectedIngredientCount
        );
    }

    private Visit getVisitOrThrow(Long visitId) {
        return visitRepository.findById(visitId)
            .orElseThrow(() -> new IllegalArgumentException(
                "탐방 세션을 찾을 수 없습니다. visitId=" + visitId));
    }
}
