package com.tourismdata.contest.external.tourapi.dto;

import com.tourismdata.contest.domain.nearby.entity.PlaceType;

// TourAPI 원본 응답을 정규화한 내부 DTO. NearbyPlaceSyncScheduler에서 NearbyPlace로 변환.
public record TourApiPlaceDto(
    String contentId,
    PlaceType type,
    String name,
    String address,
    Double latitude,
    Double longitude,
    String imageUrl
) {
}
