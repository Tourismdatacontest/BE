package com.tourismdata.contest.domain.nearby.repository;

import com.tourismdata.contest.domain.nearby.entity.NearbyPlace;
import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NearbyPlaceRepository extends JpaRepository<NearbyPlace, Long> {

    List<NearbyPlace> findByType(PlaceType type);

    // ETL upsert 조회용 (source + externalContentId 조합으로 중복 방지)
    Optional<NearbyPlace> findBySourceAndExternalContentId(String source, String externalContentId);

    // CourseRecommendedPlace 큐레이션 항목을 실제 데이터로 채울 때 사용
    Optional<NearbyPlace> findFirstByExternalContentIdAndType(String externalContentId, PlaceType type);
}
