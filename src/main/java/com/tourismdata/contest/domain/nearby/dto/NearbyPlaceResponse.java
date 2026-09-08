package com.tourismdata.contest.domain.nearby.dto;

import com.tourismdata.contest.domain.nearby.entity.NearbyPlace;
import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import lombok.Getter;

// GET /nearby 응답 (swagger NearbyPlace 스키마와 필드 동일)
@Getter
public class NearbyPlaceResponse {

    private final String externalContentId;
    private final PlaceType type;
    private final String name;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final String imageUrl;

    private NearbyPlaceResponse(NearbyPlace entity) {
        this.externalContentId = entity.getExternalContentId();
        this.type = entity.getType();
        this.name = entity.getName();
        this.address = entity.getAddress();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.imageUrl = entity.getImageUrl();
    }

    public static NearbyPlaceResponse from(NearbyPlace entity) {
        return new NearbyPlaceResponse(entity);
    }
}
