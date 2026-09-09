package com.tourismdata.contest.domain.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.entity.RoutePoint;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.course.repository.RoutePointRepository;
import com.tourismdata.contest.external.tourapi.CheckpointTourApiClient;
import com.tourismdata.contest.external.tourapi.dto.CheckpointTourApiPlaceDto;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

// TourAPI(공공데이터포털) 검색 결과 중 관광지(contentTypeId=12)를 체크포인트로 가져오는 운영 도구.
// name/좌표/이미지는 TourAPI 원본을 쓰지만, guideContent(안내 서사)는 TourAPI 주소값으로
// 임시 채워질 뿐이므로 실제 병자호란 스토리 콘텐츠는 이후 콘텐츠 담당자가 교체해야 한다.
@Service
@RequiredArgsConstructor
@Transactional
public class CourseAdminService {

    private static final String TOUR_API_ATTRACTION_TYPE = "12";

    private final CourseRepository courseRepository;
    private final CheckpointRepository checkpointRepository;
    private final RoutePointRepository routePointRepository;
    private final CheckpointTourApiClient tourApiClient;

    public List<CheckpointResponse> importCheckpointsFromTourApi(Long courseId, String keyword) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        int nextOrderNo = checkpointRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId).size() + 1;

        List<CheckpointTourApiPlaceDto> places = tourApiClient.searchKeyword(keyword).stream()
                .filter(place -> TOUR_API_ATTRACTION_TYPE.equals(place.contentTypeId()))
                .filter(place -> place.latitude() != null && place.longitude() != null)
                .toList();

        List<Checkpoint> checkpoints = new ArrayList<>();
        int orderNo = nextOrderNo;
        for (CheckpointTourApiPlaceDto place : places) {
            checkpoints.add(Checkpoint.builder()
                    .course(course)
                    .orderNo(orderNo++)
                    .name(place.title())
                    .latitude(place.latitude())
                    .longitude(place.longitude())
                    .imageUrl(place.firstImage())
                    .guideContent(place.addr1())
                    .build());
        }

        checkpointRepository.saveAll(checkpoints);
        return checkpoints.stream().map(CheckpointResponse::from).toList();
    }

    // 정밀 GPS 트레일 데이터 소스가 없어서, 이미 실좌표로 확보된 체크포인트들을 순서대로
    // 이어 붙여 경로로 쓴다 (지어낸 좌표가 아니라 전부 실제 위치 기반). 재실행해도 안전하도록
    // 기존 RoutePoint를 지우고 다시 만든다.
    public List<RoutePointResponse> generateRouteFromCheckpoints(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        List<Checkpoint> checkpoints = checkpointRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId);

        // RoutePoint는 IDENTITY 전략이라 saveAll이 각 행을 즉시 insert한다.
        // flush 없이 두면 Hibernate 액션 큐 순서상 delete가 insert보다 늦게 실행되어
        // (course_id, sequence) unique 제약과 충돌하므로 delete를 먼저 flush로 확정한다.
        routePointRepository.deleteByCourse_CourseId(courseId);
        routePointRepository.flush();

        List<RoutePoint> routePoints = new ArrayList<>();
        int sequence = 1;
        for (Checkpoint checkpoint : checkpoints) {
            routePoints.add(RoutePoint.builder()
                    .course(course)
                    .sequence(sequence++)
                    .latitude(checkpoint.getLatitude())
                    .longitude(checkpoint.getLongitude())
                    .build());
        }

        routePointRepository.saveAll(routePoints);
        return routePoints.stream().map(RoutePointResponse::from).toList();
    }
}
