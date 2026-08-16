package com.tourismdata.contest.domain.course.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseDetailResponse;
import com.tourismdata.contest.domain.course.dto.CourseSummaryResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.course.repository.RoutePointRepository;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final RoutePointRepository routePointRepository;
    private final CheckpointRepository checkpointRepository;

    public List<CourseSummaryResponse> getCourses() {
        return courseRepository.findAll().stream()
                .map(CourseSummaryResponse::from)
                .toList();
    }

    public CourseDetailResponse getCourseDetail(Long courseId) {
        return CourseDetailResponse.from(findCourseOrThrow(courseId));
    }

    public List<RoutePointResponse> getRoute(Long courseId) {
        findCourseOrThrow(courseId);
        return routePointRepository.findByCourse_CourseIdOrderBySequenceAsc(courseId).stream()
                .map(RoutePointResponse::from)
                .toList();
    }

    public List<CheckpointResponse> getCheckpoints(Long courseId) {
        findCourseOrThrow(courseId);
        return checkpointRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId).stream()
                .map(CheckpointResponse::from)
                .toList();
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
    }
}
