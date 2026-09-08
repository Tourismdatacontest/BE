package com.tourismdata.contest.external.tourapi;

import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import com.tourismdata.contest.external.tourapi.dto.TourApiLocationResponse;
import com.tourismdata.contest.external.tourapi.dto.TourApiPlaceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// 한국관광공사 TourAPI 호출 클라이언트 (ETL 소스)
// ⚠️ application-local.yml의 base-url이 KorService1인데, TourAPI가 KorService2로 이전된 지
//    오래라 실제 호출되는지 먼저 확인 필요 (안 되면 base-url부터 KorService2로 교체해야 함).
// cat3 카페 코드(A05020900)도 추정치라 실제 응답으로 검증 필요.
@Component
public class TourApiClient {

    private static final String CONTENT_TYPE_LODGING = "32";
    private static final String CONTENT_TYPE_FOOD = "39";
    private static final String CAT3_CAFE = "A05020900";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${external.tourapi.base-url}")
    private String baseUrl;

    @Value("${external.tourapi.service-key}")
    private String serviceKey;

    @Value("${external.tourapi.map-x:127.1815}")
    private double mapX;

    @Value("${external.tourapi.map-y:37.4788}")
    private double mapY;

    @Value("${external.tourapi.radius-meters:2000}")
    private int radiusMeters;

    public List<TourApiPlaceDto> fetchRestaurantsAndLodging() {
        List<TourApiPlaceDto> result = new ArrayList<>();
        result.addAll(fetchByContentType(CONTENT_TYPE_FOOD));
        result.addAll(fetchByContentType(CONTENT_TYPE_LODGING));
        return result;
    }

    private List<TourApiPlaceDto> fetchByContentType(String contentTypeId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/locationBasedList2")
            .queryParam("serviceKey", serviceKey)
            .queryParam("MobileOS", "ETC")
            .queryParam("MobileApp", "NamhansanseongWalk")
            .queryParam("_type", "json")
            .queryParam("numOfRows", 100)
            .queryParam("pageNo", 1)
            .queryParam("arrangeType", "E")
            .queryParam("mapX", mapX)
            .queryParam("mapY", mapY)
            .queryParam("radius", radiusMeters)
            .queryParam("contentTypeId", contentTypeId)
            .build(true)
            .toUri();

        TourApiLocationResponse response = restTemplate.getForObject(uri, TourApiLocationResponse.class);
        List<TourApiLocationResponse.Item> items = extractItems(response);

        return items.stream()
            .map(item -> toDto(item, contentTypeId))
            .filter(Objects::nonNull)
            .toList();
    }

    private List<TourApiLocationResponse.Item> extractItems(TourApiLocationResponse response) {
        if (response == null || response.response() == null || response.response().body() == null
            || response.response().body().items() == null || response.response().body().items().item() == null) {
            return Collections.emptyList();
        }
        return response.response().body().items().item();
    }

    private TourApiPlaceDto toDto(TourApiLocationResponse.Item item, String contentTypeId) {
        PlaceType type = resolveType(item, contentTypeId);
        if (type == null) {
            return null;
        }
        Double lat = parse(item.mapy());
        Double lng = parse(item.mapx());
        if (lat == null || lng == null) {
            return null;
        }
        return new TourApiPlaceDto(item.contentid(), type, item.title(), item.addr1(), lat, lng, item.firstimage());
    }

    private PlaceType resolveType(TourApiLocationResponse.Item item, String contentTypeId) {
        if (CONTENT_TYPE_LODGING.equals(contentTypeId)) {
            return PlaceType.LODGING;
        }
        if (CONTENT_TYPE_FOOD.equals(contentTypeId)) {
            return CAT3_CAFE.equals(item.cat3()) ? PlaceType.CAFE : PlaceType.RESTAURANT;
        }
        return null;
    }

    private Double parse(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
