package com.tourismdata.contest.domain.course.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.course.dto.CourseSummaryResponse;
import com.tourismdata.contest.domain.course.dto.RoutePointResponse;
import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.entity.RoutePoint;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.course.repository.RoutePointRepository;
import com.tourismdata.contest.domain.nearby.repository.CourseRecommendedPlaceRepository;
import com.tourismdata.contest.domain.story.repository.StoryEventRepository;
import com.tourismdata.contest.domain.story.repository.VisitIngredientRepository;
import com.tourismdata.contest.domain.visit.repository.VisitLocationLogRepository;
import com.tourismdata.contest.domain.visit.repository.VisitRepository;
import com.tourismdata.contest.external.tourapi.CheckpointTourApiClient;
import com.tourismdata.contest.external.tourapi.PhotoGalleryClient;
import com.tourismdata.contest.external.tourapi.dto.CheckpointTourApiDetailDto;
import com.tourismdata.contest.external.tourapi.dto.CheckpointTourApiPlaceDto;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;
import com.tourismdata.contest.global.util.GeoUtils;

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
    private final VisitRepository visitRepository;
    private final VisitLocationLogRepository visitLocationLogRepository;
    private final VisitIngredientRepository visitIngredientRepository;
    private final StoryEventRepository storyEventRepository;
    private final CourseRecommendedPlaceRepository courseRecommendedPlaceRepository;
    private final CheckpointTourApiClient tourApiClient;
    private final PhotoGalleryClient photoGalleryClient;

    private static final String REGION_ADDRESS_HINT = "남한산성";

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

    // 기획 스토리라인 기준 5개 코스(수장/승려/승병장/무관/인조)를 실좌표 체크포인트와 함께
    // 새로 심는 운영 도구. TourAPI 키워드 검색으로 채워뒀던 placeholder 데이터와는 완전히
    // 무관한 실제 서비스 데이터라, 기존 코스/체크포인트/경로를 전부 지우고 재생성한다.
    // 재실행해도 항상 같은 5개 코스로 초기화되도록 멱등하게 동작한다.
    //
    // ⚠️ 파괴적 작업: course/checkpoint에 FK로 걸린 visit/visit_location_log/visit_ingredient/
    // story_event/course_recommended_place(Nearby 도메인 큐레이션 테이블)까지 전부 함께 지운다.
    // 실사용자 탐방 기록이 쌓이기 전(서비스 오픈 전 1회성 데이터 세팅, 혹은 로컬 개발 환경 초기화)
    // 용도로만 호출해야 한다.
    public List<CourseSummaryResponse> seedStoryCourses() {
        visitIngredientRepository.deleteAllInBatch();
        storyEventRepository.deleteAllInBatch();
        visitLocationLogRepository.deleteAllInBatch();
        visitRepository.deleteAllInBatch();
        courseRecommendedPlaceRepository.deleteAllInBatch();
        routePointRepository.deleteAllInBatch();
        checkpointRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();

        List<CourseSummaryResponse> created = new ArrayList<>();
        for (StoryCourseSeedData.CourseSeed courseSeed : StoryCourseSeedData.COURSES) {
            List<StoryCourseSeedData.CheckpointSeed> checkpointSeeds = courseSeed.checkpoints();

            // 실제 트레일 경로 데이터가 없어, 체크포인트 간 직선거리 합산으로 대략치를 낸다.
            // 실제 도보 경로는 직선보다 길기 때문에 team이 나중에 실측치로 교체해야 한다.
            int distanceM = (int) Math.round(totalStraightLineDistance(checkpointSeeds));
            int estimatedMinutes = Math.max(1, (int) Math.ceil(distanceM / 50.0));

            Course course = courseRepository.save(Course.builder()
                    .title(courseSeed.title())
                    .description(courseSeed.description())
                    .thumbnailUrl(null)
                    .distanceM(distanceM)
                    .estimatedMinutes(estimatedMinutes)
                    .difficulty(courseSeed.difficulty())
                    .build());

            List<Checkpoint> checkpoints = new ArrayList<>();
            int orderNo = 1;
            for (StoryCourseSeedData.CheckpointSeed checkpointSeed : checkpointSeeds) {
                checkpoints.add(Checkpoint.builder()
                        .course(course)
                        .orderNo(orderNo++)
                        .name(checkpointSeed.name())
                        .latitude(checkpointSeed.latitude())
                        .longitude(checkpointSeed.longitude())
                        .guideContent(checkpointSeed.guideContent())
                        .imageUrl(null)
                        .build());
            }
            checkpointRepository.saveAll(checkpoints);

            generateRouteFromCheckpoints(course.getCourseId());
            created.add(CourseSummaryResponse.from(course));
        }

        return created;
    }

    // 한국관광공사 TourAPI(KorService2 상세조회 + PhotoGalleryService1)로 체크포인트
    // guideContent/imageUrl을 보강하는 운영 도구. 이름이 같은 체크포인트(코스끼리 겹치는
    // 물리적 장소)는 한 번만 조회하도록 캐싱한다. 매칭 안 되는 체크포인트(성문류, 옹성 등
    // 마이너한 유적)는 기존 값을 그대로 둔다 - 개별 호출 실패도 전체를 막지 않도록
    // 하나씩 방어적으로 처리한다.
    public List<CheckpointResponse> enrichCheckpointsFromTourApi() {
        List<Checkpoint> checkpoints = checkpointRepository.findAll();
        Map<String, TourApiEnrichment> cache = new HashMap<>();

        for (Checkpoint checkpoint : checkpoints) {
            TourApiEnrichment enrichment = cache.computeIfAbsent(checkpoint.getName(), this::fetchEnrichment);
            checkpoint.enrichFromTourApi(enrichment.overview(), enrichment.imageUrl());
        }

        return checkpoints.stream().map(CheckpointResponse::from).toList();
    }

    private TourApiEnrichment fetchEnrichment(String checkpointName) {
        try {
            String coreName = stripParenthetical(checkpointName);
            Optional<CheckpointTourApiPlaceDto> match = findBestMatch(coreName);
            if (match.isEmpty()) {
                return TourApiEnrichment.EMPTY;
            }

            CheckpointTourApiPlaceDto place = match.get();
            String overview = null;
            String imageUrl = place.firstImage();

            try {
                Optional<CheckpointTourApiDetailDto> detail = tourApiClient.fetchDetail(place.contentId());
                if (detail.isPresent()) {
                    overview = detail.get().overview();
                    if (isBlank(imageUrl)) {
                        imageUrl = detail.get().firstImage();
                    }
                }
            } catch (Exception e) {
                // detailCommon2 실패해도 검색 결과로 얻은 정보만으로 계속 진행
            }

            if (isBlank(imageUrl)) {
                try {
                    imageUrl = photoGalleryClient.searchFirstImage(coreName).orElse(null);
                } catch (Exception e) {
                    // 사진 갤러리 조회 실패는 무시하고 사진 없이 진행
                }
            }

            return new TourApiEnrichment(overview, imageUrl);
        } catch (Exception e) {
            // 이 체크포인트는 보강 실패 - 기존 값 유지
            return TourApiEnrichment.EMPTY;
        }
    }

    // "남한산성"을 붙이지 않은 이름과 붙인 이름 둘 다 시도하고, 주소에 "남한산성"이 포함된
    // 결과만 채택한다 (예: "국청사"로만 검색하면 부산의 동명 사찰이 걸릴 수 있음).
    private Optional<CheckpointTourApiPlaceDto> findBestMatch(String coreName) {
        for (String query : List.of(coreName, REGION_ADDRESS_HINT + " " + coreName)) {
            try {
                Optional<CheckpointTourApiPlaceDto> match = tourApiClient.searchKeyword(query).stream()
                        .filter(place -> place.addr1() != null && place.addr1().contains(REGION_ADDRESS_HINT))
                        .findFirst();
                if (match.isPresent()) {
                    return match;
                }
            } catch (Exception e) {
                // 이 쿼리 변형은 실패 - 다음 변형 시도
            }
        }
        return Optional.empty();
    }

    private static String stripParenthetical(String name) {
        int idx = name.indexOf('(');
        return idx > 0 ? name.substring(0, idx).trim() : name;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record TourApiEnrichment(String overview, String imageUrl) {
        static final TourApiEnrichment EMPTY = new TourApiEnrichment(null, null);
    }

    private double totalStraightLineDistance(List<StoryCourseSeedData.CheckpointSeed> checkpoints) {
        double total = 0;
        for (int i = 1; i < checkpoints.size(); i++) {
            StoryCourseSeedData.CheckpointSeed prev = checkpoints.get(i - 1);
            StoryCourseSeedData.CheckpointSeed curr = checkpoints.get(i);
            total += GeoUtils.distanceInMeters(prev.latitude(), prev.longitude(), curr.latitude(), curr.longitude());
        }
        return total;
    }
}
