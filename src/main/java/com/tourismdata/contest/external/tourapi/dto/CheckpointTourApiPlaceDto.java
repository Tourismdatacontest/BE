package com.tourismdata.contest.external.tourapi.dto;

// TourAPI 원본 응답에서 실제로 쓰는 필드만 정제한 형태 (Course/Checkpoint 시딩 전용).
// 같은 external.tourapi 패키지에 Nearby 도메인이 쓰는 TourApiPlaceDto가 이미 있어서
// (locationBasedList2 기반, PlaceType 필드 등 shape가 다름) 이름을 분리했다.
public record CheckpointTourApiPlaceDto(
        String contentId,
        String contentTypeId,
        String title,
        String addr1,
        String firstImage,
        Double latitude,
        Double longitude
) {
}
