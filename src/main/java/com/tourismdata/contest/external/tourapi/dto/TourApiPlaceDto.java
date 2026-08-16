package com.tourismdata.contest.external.tourapi.dto;

// TourAPI 원본 응답에서 실제로 쓰는 필드만 정제한 형태
public record TourApiPlaceDto(
        String contentId,
        String contentTypeId,
        String title,
        String addr1,
        String firstImage,
        Double latitude,
        Double longitude
) {
}
