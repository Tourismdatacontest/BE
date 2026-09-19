package com.tourismdata.contest.external.tourapi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 한국관광공사 국문 관광정보 서비스(KorService2)의 단일 장소 상세 조회 클라이언트
// (detailCommon2 + detailIntro2). 홈 화면 "남한산성 소개"처럼 특정 contentId 하나의
// 공식 소개 정보를 실시간으로 가져오는 용도.
@Component
public class TourSpotInfoClient {

    private final RestClient restClient;
    private final String serviceKey;

    public TourSpotInfoClient(@Value("${external.tourapi.base-url}") String baseUrl,
                              @Value("${external.tourapi.service-key}") String serviceKey) {
        this.restClient = RestClient.create(baseUrl);
        this.serviceKey = serviceKey;
    }

    public Optional<Common> fetchCommon(String contentId) {
        CommonResponse response = get("/detailCommon2", contentId, null, CommonResponse.class);
        if (response == null || response.response() == null || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null
                || response.response().body().items().item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.response().body().items().item().get(0));
    }

    // detailIntro2는 contentTypeId가 필수 (남한산성도립공원 = 12, 관광지).
    public Optional<Intro> fetchIntro(String contentId, String contentTypeId) {
        IntroResponse response = get("/detailIntro2", contentId, contentTypeId, IntroResponse.class);
        if (response == null || response.response() == null || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null
                || response.response().body().items().item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.response().body().items().item().get(0));
    }

    private <T> T get(String path, String contentId, String contentTypeId, Class<T> type) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path)
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("MobileOS", "ETC")
                            .queryParam("MobileApp", "TourismdataContest")
                            .queryParam("_type", "json")
                            .queryParam("contentId", contentId);
                    if (contentTypeId != null) {
                        uriBuilder.queryParam("contentTypeId", contentTypeId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(type);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CommonResponse(CommonRoot response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CommonRoot(CommonBody body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CommonBody(CommonItems items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CommonItems(@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<Common> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Common(String title, String overview, String addr1, String homepage, String firstimage) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IntroResponse(IntroRoot response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IntroRoot(IntroBody body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IntroBody(IntroItems items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IntroItems(@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<Intro> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Intro(String infocenter, String restdate, String usetime, String parking) {
    }
}
