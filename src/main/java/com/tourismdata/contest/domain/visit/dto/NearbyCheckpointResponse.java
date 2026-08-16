package com.tourismdata.contest.domain.visit.dto;

import com.tourismdata.contest.domain.course.entity.Checkpoint;

// Developer A(Location) 생성 -> Developer B(Story) 소비.
// 위치 갱신 시 다음 체크포인트 근접 판정 결과를 Story 도메인이 이벤트 트리거로 쓸 수 있게
// LocationTrackingService.checkNearbyCheckpoint()가 반환하는 내부 데이터 계약.
public record NearbyCheckpointResponse(
        Long checkpointId,
        Integer orderNo,
        String name,
        double distanceMeters
) {

    public static NearbyCheckpointResponse of(Checkpoint checkpoint, double distanceMeters) {
        return new NearbyCheckpointResponse(
                checkpoint.getCheckpointId(),
                checkpoint.getOrderNo(),
                checkpoint.getName(),
                distanceMeters
        );
    }
}
