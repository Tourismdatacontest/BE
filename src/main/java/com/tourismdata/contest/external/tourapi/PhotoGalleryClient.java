package com.tourismdata.contest.external.tourapi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 한국관광공사_관광사진 정보_GW(PhotoGalleryService1) 연동 클라이언트.
// data.go.kr에 KorService2(검색/상세)와 별도로 등록된 API지만, 같은 공공데이터포털
// 서비스키를 그대로 쓴다. 체크포인트 imageUrl 보강(관리자 도구)에만 쓴다.
@Component
public class PhotoGalleryClient {

    private final RestClient restClient;
    private final String serviceKey;

    public PhotoGalleryClient(@Value("${external.tourapi.photo-base-url}") String baseUrl,
                               @Value("${external.tourapi.service-key}") String serviceKey) {
        this.restClient = RestClient.create(baseUrl);
        this.serviceKey = serviceKey;
    }

    /**
     * 키워드로 관광사진을 검색해 첫 번째 결과의 이미지 URL을 반환한다.
     * 마이너한 장소는 결과가 아예 없는 경우가 많아 Optional로 감싼다.
     */
    public Optional<String> searchFirstImage(String keyword) {
        return search(keyword, 1).stream()
                .findFirst()
                .map(GalleryItem::galWebImageUrl);
    }

    /**
     * 키워드로 관광사진을 검색해 결과 목록을 그대로 반환한다 (실시간 호출, 저장하지 않음).
     * 홈 화면 배너처럼 매 요청마다 살아있는 공공데이터를 그대로 보여줘야 하는 용도.
     */
    public List<GalleryItem> search(String keyword, int numOfRows) {
        GalleryResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/gallerySearchList1")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "TourismdataContest")
                        .queryParam("_type", "json")
                        .queryParam("keyword", keyword)
                        .build())
                .retrieve()
                .body(GalleryResponse.class);

        return extractItems(response);
    }

    private static List<GalleryItem> extractItems(GalleryResponse response) {
        if (response == null || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null) {
            return List.of();
        }
        return response.response().body().items().item();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GalleryResponse(GalleryResponseBody response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GalleryResponseBody(GalleryBody body) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GalleryBody(GalleryItems items) {
    }

    // TourAPI 계열은 결과 1건일 때 item이 배열이 아니라 단일 객체로 오는 경우가 있어 방어.
    @JsonIgnoreProperties(ignoreUnknown = true)
    record GalleryItems(@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<GalleryItem> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GalleryItem(String galContentId, String galTitle, String galWebImageUrl) {
    }
}
