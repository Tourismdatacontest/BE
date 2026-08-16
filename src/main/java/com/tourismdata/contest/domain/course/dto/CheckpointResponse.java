package com.tourismdata.contest.domain.course.dto;

import com.tourismdata.contest.domain.course.entity.Checkpoint;

public record CheckpointResponse(
        Long checkpointId,
        Long courseId,
        Integer orderNo,
        String name,
        Double latitude,
        Double longitude,
        String guideContent,
        String imageUrl
) {

    public static CheckpointResponse from(Checkpoint checkpoint) {
        return new CheckpointResponse(
                checkpoint.getCheckpointId(),
                checkpoint.getCourse().getCourseId(),
                checkpoint.getOrderNo(),
                checkpoint.getName(),
                checkpoint.getLatitude(),
                checkpoint.getLongitude(),
                checkpoint.getGuideContent(),
                checkpoint.getImageUrl()
        );
    }
}
