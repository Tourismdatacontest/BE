package com.tourismdata.contest.batch;

import com.tourismdata.contest.domain.nearby.entity.NearbyPlace;
import com.tourismdata.contest.domain.nearby.repository.NearbyPlaceRepository;
import com.tourismdata.contest.external.tourapi.TourApiClient;
import com.tourismdata.contest.external.tourapi.dto.TourApiPlaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 매일 새벽 3시 TourAPI 데이터를 nearby_places 테이블에 적재.
// Kakao Local(포토존 등 TourAPI 미커버 카테고리)은 아직 미구현 - 추후 별도 추가.
// ⚠️ ContestApplication.java에 @EnableScheduling 없으면 동작 안 함, 확인 필요.
@Slf4j
@Component
@RequiredArgsConstructor
public class NearbyPlaceSyncScheduler {

    private static final String SOURCE_TOURAPI = "TOURAPI";

    private final TourApiClient tourApiClient;
    private final NearbyPlaceRepository nearbyPlaceRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void syncFromTourApi() {
        List<TourApiPlaceDto> places = tourApiClient.fetchRestaurantsAndLodging();
        int upserted = 0;

        for (TourApiPlaceDto dto : places) {
            upsert(dto);
            upserted++;
        }

        log.info("TourAPI nearby place sync complete. upserted={}", upserted);
    }

    private void upsert(TourApiPlaceDto dto) {
        nearbyPlaceRepository.findBySourceAndExternalContentId(SOURCE_TOURAPI, dto.contentId())
            .ifPresentOrElse(
                existing -> existing.updateFrom(dto.name(), dto.address(), dto.latitude(), dto.longitude(), dto.imageUrl()),
                () -> nearbyPlaceRepository.save(NearbyPlace.builder()
                    .externalContentId(dto.contentId())
                    .type(dto.type())
                    .name(dto.name())
                    .address(dto.address())
                    .latitude(dto.latitude())
                    .longitude(dto.longitude())
                    .imageUrl(dto.imageUrl())
                    .source(SOURCE_TOURAPI)
                    .build())
            );
    }
}
