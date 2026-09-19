package com.tourismdata.contest.external.tourapi;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tourismdata.contest.external.tourapi.dto.CheckpointTourApiDetailDto;
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
        // 타임아웃이 없으면 외부 API 지연 시 요청/캐시 갱신이 무한정 대기한다.
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
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

    /**
     * contentId로 상세 정보(개요/사진)를 조회한다 (detailCommon2).
     * 실무 확인 결과 defaultYN 등 YN 플래그 파라미터를 넣으면 오히려
     * INVALID_REQUEST_PARAMETER_ERROR가 나서 최소 파라미터만 사용한다.
     */
    public Optional<CheckpointTourApiDetailDto> fetchDetail(String contentId) {
        DetailResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/detailCommon2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "TourismdataContest")
                        .queryParam("_type", "json")
                        .queryParam("contentId", contentId)
                        .build())
                .retrieve()
                .body(DetailResponse.class);

        return extractDetailItem(response)
                .map(item -> new CheckpointTourApiDetailDto(item.overview(), item.firstimage()));
    }

    private static Optional<DetailItem> extractDetailItem(DetailResponse response) {
        if (response == null || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null
                || response.response().body().items().item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.response().body().items().item().get(0));
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

    // TourAPI는 결과가 1건이면 item이 배열이 아니라 단일 객체로 오는 경우가 있어
    // ACCEPT_SINGLE_VALUE_AS_ARRAY로 방어한다. (0건일 때는 items 자체가 빈 문자열로
    // 오는 별도 케이스라 이 애노테이션만으로는 못 막고, 호출부에서 try-catch로 방어한다.)
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Items(@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<Item> item) {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetailResponse(DetailResponseBody response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetailResponseBody(DetailBody body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetailBody(DetailItems items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetailItems(@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<DetailItem> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetailItem(String overview, String firstimage) {
    }
}
