package com.tourismdata.contest.domain.nearby.service;

import com.tourismdata.contest.domain.nearby.dto.NearbyPlaceResponse;
import com.tourismdata.contest.domain.nearby.entity.CourseRecommendedPlace;
import com.tourismdata.contest.domain.nearby.entity.NearbyPlace;
import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import com.tourismdata.contest.domain.nearby.repository.CourseRecommendedPlaceRepository;
import com.tourismdata.contest.domain.nearby.repository.NearbyPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// ETL 방식으로 확정(팀 결정) - nearby_places 테이블을 조회.
// courseId가 있으면 CourseRecommendedPlace 큐레이션 순서를 따르고,
// 없으면 nearby_places 전체(타입 필터만 적용)를 반환한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NearbyService {

    private final NearbyPlaceRepository nearbyPlaceRepository;
    private final CourseRecommendedPlaceRepository courseRecommendedPlaceRepository;

    public List<NearbyPlaceResponse> getNearbyPlaces(Long courseId, PlaceType type) {
        if (courseId != null) {
            return getCuratedPlaces(courseId, type);
        }
        return getAllPlaces(type);
    }

    private List<NearbyPlaceResponse> getCuratedPlaces(Long courseId, PlaceType type) {
        List<CourseRecommendedPlace> curated =
            courseRecommendedPlaceRepository.findByCourse_CourseIdOrderByOrderNoAsc(courseId);

        return curated.stream()
            .filter(cr -> type == null || cr.getType() == type)
            .map(cr -> nearbyPlaceRepository.findFirstByExternalContentIdAndType(
                cr.getExternalContentId(), cr.getType()))
            .flatMap(Optional::stream)
            .map(NearbyPlaceResponse::from)
            .toList();
    }

    private List<NearbyPlaceResponse> getAllPlaces(PlaceType type) {
        List<NearbyPlace> places = (type != null)
            ? nearbyPlaceRepository.findByType(type)
            : nearbyPlaceRepository.findAll();

        return places.stream()
            .map(NearbyPlaceResponse::from)
            .toList();
    }
}
