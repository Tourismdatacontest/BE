package com.tourismdata.contest.domain.course.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
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
}
