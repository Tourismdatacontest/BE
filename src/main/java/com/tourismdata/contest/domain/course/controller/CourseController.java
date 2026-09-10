package com.tourismdata.contest.domain.course.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseDetailResponse;
import com.tourismdata.contest.domain.course.dto.CourseSummaryResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.service.CourseService;

import lombok.RequiredArgsConstructor;

// GET /courses, /courses/{courseId}, /courses/{courseId}/route, /courses/{courseId}/checkpoints
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseSummaryResponse> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/{courseId}")
    public CourseDetailResponse getCourseDetail(@PathVariable Long courseId) {
        return courseService.getCourseDetail(courseId);
    }

    @GetMapping("/{courseId}/route")
    public List<RoutePointResponse> getRoute(@PathVariable Long courseId) {
        return courseService.getRoute(courseId);
    }

    @GetMapping("/{courseId}/checkpoints")
    public List<CheckpointResponse> getCheckpoints(@PathVariable Long courseId) {
        return courseService.getCheckpoints(courseId);
    }
}
