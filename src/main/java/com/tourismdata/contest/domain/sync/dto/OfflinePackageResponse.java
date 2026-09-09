package com.tourismdata.contest.domain.sync.dto;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseDetailResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.story.dto.StoryEventResponse;

import java.util.List;

public record OfflinePackageResponse(
    Long courseId,
    CourseDetailResponse course,
    List<RoutePointResponse> routePoints,
    List<CheckpointResponse> checkpoints,
    List<StoryEventResponse> storyEvents
) {
}
