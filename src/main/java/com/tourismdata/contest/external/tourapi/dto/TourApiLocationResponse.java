package com.tourismdata.contest.external.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// locationBasedList2 원본 JSON(_type=json) 매핑용. TourApiClient 내부에서만 사용.
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiLocationResponse(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Body body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(Items items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(List<Item> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
        String contentid,
        String contenttypeid,
        String title,
        String addr1,
        String mapx,
        String mapy,
        String firstimage,
        String cat3
    ) {
    }
}
