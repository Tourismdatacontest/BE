package com.tourismdata.contest.external.tourapi.dto;

// detailCommon2 조회 결과 중 체크포인트 정보 보강(guideContent/imageUrl 채우기)에 쓰는 필드만 정제.
public record CheckpointTourApiDetailDto(
        String overview,
        String firstImage
) {
}
