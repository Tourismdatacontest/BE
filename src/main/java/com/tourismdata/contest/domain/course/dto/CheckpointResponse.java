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
    public static CheckpointResponse from(Checkpoint entity) {
        return new CheckpointResponse(
            entity.getCheckpointId(),
            entity.getCourse().getCourseId(),
            entity.getOrderNo(),
            entity.getName(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getGuideContent(),
            entity.getImageUrl()
        );
    }
}
