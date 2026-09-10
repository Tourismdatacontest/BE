package com.tourismdata.contest.domain.course.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseSummaryResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.service.CourseAdminService;

import lombok.RequiredArgsConstructor;

// 공공데이터포털(TourAPI) 연동으로 체크포인트를 채워 넣는 내부 운영 도구.
// OpenAPI 명세에 없는 데이터 관리용 엔드포인트라 프론트 연동 대상이 아니다.
@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class CourseAdminController {

    private final CourseAdminService courseAdminService;

    @PostMapping("/{courseId}/checkpoints/sync-tourapi")
    public List<CheckpointResponse> syncCheckpointsFromTourApi(@PathVariable Long courseId,
                                                                 @RequestParam String keyword) {
        return courseAdminService.importCheckpointsFromTourApi(courseId, keyword);
    }

    @PostMapping("/{courseId}/route/generate-from-checkpoints")
    public List<RoutePointResponse> generateRouteFromCheckpoints(@PathVariable Long courseId) {
        return courseAdminService.generateRouteFromCheckpoints(courseId);
    }

    // 기획 스토리라인 기준 5개 코스(수장/승려/승병장/무관/인조)를 실좌표 체크포인트와 함께
    // 새로 심는다. 호출 시 기존 코스/체크포인트/경로 데이터를 전부 지우고 재생성한다.
    @PostMapping("/seed-story-courses")
    public List<CourseSummaryResponse> seedStoryCourses() {
        return courseAdminService.seedStoryCourses();
    }
}
