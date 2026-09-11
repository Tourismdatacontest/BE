package com.tourismdata.contest.domain.story.controller;

import com.tourismdata.contest.domain.story.dto.StoryEventResponse;
import com.tourismdata.contest.domain.story.service.StoryAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// OpenAPI 명세에 없는 데이터 관리용 엔드포인트 - 프론트 연동 대상 아님.
@RestController
@RequiredArgsConstructor
public class StoryAdminController {

    private final StoryAdminService storyAdminService;

    // 검증된 코스(현재 1코스만)의 story_events + 재료를 심음.
    // 2~5코스는 체크포인트 구성이 A의 시딩 데이터와 불일치해서 기획 확인 후
    // StoryContentSeedData에 추가하면 이 엔드포인트로 같이 심어짐.
    @PostMapping("/admin/story/seed-verified-courses")
    public List<StoryEventResponse> seedVerifiedCourseContent() {
        return storyAdminService.seedVerifiedCourseContent();
    }
}
