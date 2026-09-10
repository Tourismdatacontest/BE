package com.tourismdata.contest.external.tourapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tourismdata.contest.external.tourapi.dto.CheckpointTourApiPlaceDto;

// 한국관광공사 TourAPI(KorService2) 연동 클라이언트 - Course/Checkpoint 시딩(관리자 도구) 전용.
// 같은 패키지의 TourApiClient(Nearby 도메인, locationBasedList2 기반)와는 목적이 달라 분리했다.
// 공공데이터포털에서 발급받은 서비스키로 관광지/음식점/행사 정보를 키워드 검색한다.
@Component
public class CheckpointTourApiClient {

    private final RestClient restClient;
    private final String serviceKey;

    public CheckpointTourApiClient(@Value("${external.tourapi.base-url}") String baseUrl,
                                    @Value("${external.tourapi.service-key}") String serviceKey) {
        this.restClient = RestClient.create(baseUrl);
        this.serviceKey = serviceKey;
    }

    /**
     * 키워드로 관광정보를 검색한다 (예: "남한산성").
     * TourAPI는 결과가 1건일 때 배열 대신 단일 객체를 반환하는 경우가 있어,
     * 다건 검색이 확실한 용도(코스/체크포인트 시딩)로만 사용을 권장한다.
     */
    public List<CheckpointTourApiPlaceDto> searchKeyword(String keyword) {
        SearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchKeyword2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 50)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "TourismdataContest")
                        .queryParam("_type", "json")
                        .queryParam("keyword", keyword)
                        .build())
                .retrieve()
                .body(SearchResponse.class);

        List<Item> items = extractItems(response);
        return items.stream()
                .map(item -> new CheckpointTourApiPlaceDto(
                        item.contentid(),
                        item.contenttypeid(),
                        item.title(),
                        item.addr1(),
                        item.firstimage(),
                        parseDouble(item.mapy()),
                        parseDouble(item.mapx())
                ))
                .toList();
    }

    private static List<Item> extractItems(SearchResponse response) {
        if (response == null || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null) {
            return List.of();
        }
        return response.response().body().items().item();
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchResponse(Response response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(Body body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Body(Items items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Items(List<Item> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Item(
            String contentid,
            String contenttypeid,
            String title,
            String addr1,
            String firstimage,
            String mapx,
            String mapy
    ) {
    }
}
