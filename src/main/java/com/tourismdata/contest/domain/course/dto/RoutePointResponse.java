package com.tourismdata.contest.domain.course.dto;

import com.tourismdata.contest.domain.course.entity.RoutePoint;

public record RoutePointResponse(
    Long routePointId,
    Integer sequence,
    Double latitude,
    Double longitude
) {
    public static RoutePointResponse from(RoutePoint entity) {
        return new RoutePointResponse(
            entity.getRoutePointId(),
            entity.getSequence(),
            entity.getLatitude(),
            entity.getLongitude()
        );
    }
}
