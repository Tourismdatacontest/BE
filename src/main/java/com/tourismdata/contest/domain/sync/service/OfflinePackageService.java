package com.tourismdata.contest.domain.sync.service;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseDetailResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.course.repository.RoutePointRepository;
import com.tourismdata.contest.domain.story.dto.StoryEventResponse;
import com.tourismdata.contest.domain.story.repository.StoryEventRepository;
import com.tourismdata.contest.domain.sync.dto.OfflinePackageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 오프라인 패키지 조회 서비스.
 * 코스 정보 + 경로 + 체크포인트 + 스토리 이벤트를 한 번에 묶어서 반환 -
 * 클라이언트가 코스 시작 시 이걸 통째로 캐싱해두고 오프라인 구간에서도 사용.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfflinePackageService {

    private final CourseRepository courseRepository;
    private final RoutePointRepository routePointRepository;
    private final CheckpointRepository checkpointRepository;
    private final StoryEventRepository storyEventRepository;

    public OfflinePackageResponse getOfflinePackage(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다. courseId=" + courseId));

        var routePoints = routePointRepository.findByCourse_CourseIdOrderBySequenceAsc(courseId).stream()
            .map(RoutePointResponse::from)
            .toList();

        var checkpoints = checkpointRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId).stream()
            .map(CheckpointResponse::from)
            .toList();

        var storyEvents = storyEventRepository.findByCheckpoint_Course_CourseId(courseId).stream()
            .map(StoryEventResponse::from)
            .toList();

        return new OfflinePackageResponse(
            courseId,
            CourseDetailResponse.from(course),
            routePoints,
            checkpoints,
            storyEvents
        );
    }
}
