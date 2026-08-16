package com.tourismdata.contest.domain.course.dto;

import com.tourismdata.contest.domain.course.entity.RoutePoint;

public record RoutePointResponse(
        Long routePointId,
        Integer sequence,
        Double latitude,
        Double longitude
) {

    public static RoutePointResponse from(RoutePoint routePoint) {
        return new RoutePointResponse(
                routePoint.getRoutePointId(),
                routePoint.getSequence(),
                routePoint.getLatitude(),
                routePoint.getLongitude()
        );
    }
}
