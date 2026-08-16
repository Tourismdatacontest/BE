package com.tourismdata.contest.domain.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.external.tourapi.TourApiClient;
import com.tourismdata.contest.external.tourapi.dto.TourApiPlaceDto;
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
    private final TourApiClient tourApiClient;

    public List<CheckpointResponse> importCheckpointsFromTourApi(Long courseId, String keyword) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        int nextOrderNo = checkpointRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId).size() + 1;

        List<TourApiPlaceDto> places = tourApiClient.searchKeyword(keyword).stream()
                .filter(place -> TOUR_API_ATTRACTION_TYPE.equals(place.contentTypeId()))
                .filter(place -> place.latitude() != null && place.longitude() != null)
                .toList();

        List<Checkpoint> checkpoints = new ArrayList<>();
        int orderNo = nextOrderNo;
        for (TourApiPlaceDto place : places) {
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
}
