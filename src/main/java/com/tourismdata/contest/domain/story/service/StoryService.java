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

    // 스토리라인 문서 기준 모든 코스 공통 프롤로그. 코스마다 달라지지 않음.
    private static final String COMMON_PROLOGUE = """
        사용자: "잠깐… 여기가 어디지?"
        눈을 뜬 순간, 낯선 남한산성이 펼쳐졌다. 1636년, 병자호란이 한창인 남한산성.

        그때, 낯선 존재가 나타났다.
        사용자: "으악 도깨비다!"
        도깨비: "도깨비…? 이게 나야? 나… 내가 누군지 기억이 안 나. 이 모습은 뭐지?"

        노인: "응? 너는 이 시대의 사람이 아니구나."
        노인: "원래의 시간으로 돌아가고 싶다면 전설의 백숙을 완성하거라."
        노인: "산성 곳곳에 흩어진 재료를 찾아야 한다."
        노인: (도깨비를 바라보며) "그리고 너도 함께 가거라. 잃어버린 기억을 찾게 될지도 모르니."

        전설의 백숙을 완성하세요!
        도깨비와 남한산성을 걸으며 백숙 재료를 모아보세요.
        선택한 코스를 따라 이동하고 체크포인트에 도착하면 이야기가 이어집니다.
        """;

    private final VisitRepository visitRepository;
    private final CheckpointRepository checkpointRepository;
    private final StoryEventRepository storyEventRepository;
    private final IngredientRepository ingredientRepository;
    private final VisitIngredientRepository visitIngredientRepository;

    public StoryIntroResponse getStoryIntro(Long visitId) {
        Visit visit = getVisitOrThrow(visitId);
        Course course = visit.getCourse();
        return new StoryIntroResponse(visitId, course.getTitle(), COMMON_PROLOGUE);
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
