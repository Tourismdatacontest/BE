package com.tourismdata.contest.external.tourapi;

import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import com.tourismdata.contest.external.tourapi.dto.TourApiLocationResponse;
import com.tourismdata.contest.external.tourapi.dto.TourApiPlaceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 한국관광공사 TourAPI 호출 클라이언트 (ETL 소스).
 *
 * TourAPI 응답 특이사항 방어:
 * - 결과 0건일 때 items가 객체가 아니라 빈 문자열("")로 옴 -> 타입으로 바로 역직렬화하면 실패.
 * - 결과 정확히 1건일 때 item이 배열이 아니라 단일 객체로 옴.
 * 이 두 케이스 때문에 응답을 바로 TourApiLocationResponse로 매핑하지 않고,
 * 원시 JSON을 받아 JsonNode로 방어적으로 파싱한다.
 */
@Component
public class TourApiClient {

    private static final String CONTENT_TYPE_LODGING = "32";
    private static final String CONTENT_TYPE_FOOD = "39";
    private static final String CAT3_CAFE = "A05020900";

    private final RestTemplate restTemplate = new RestTemplate();
    private final JsonMapper objectMapper = JsonMapper.builder().build();

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
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/locationBasedList2")
            .queryParam("serviceKey", serviceKey)
            .queryParam("MobileOS", "ETC")
            .queryParam("MobileApp", "NamhansanseongWalk")
            .queryParam("_type", "json")
            .queryParam("numOfRows", 100)
            .queryParam("pageNo", 1)
            .queryParam("arrange", "E")
            .queryParam("mapX", mapX)
            .queryParam("mapY", mapY)
            .queryParam("radius", radiusMeters)
            .queryParam("contentTypeId", contentTypeId)
            .build(true)
            .toUri();

        String rawJson = restTemplate.getForObject(uri, String.class);
        List<TourApiLocationResponse.Item> items = extractItems(rawJson);

        return items.stream()
            .map(item -> toDto(item, contentTypeId))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * response.body.items를 방어적으로 파싱한다.
     * - items가 객체가 아니면(빈 문자열 등) 빈 리스트 반환.
     * - item이 배열이면 각 원소를, 단일 객체면 그 하나만 파싱.
     */
    private List<TourApiLocationResponse.Item> extractItems(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return Collections.emptyList();
        }

        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode itemsNode = root.path("response").path("body").path("items");

        if (!itemsNode.isObject()) {
            return Collections.emptyList();
        }

        JsonNode itemNode = itemsNode.path("item");
        if (itemNode.isMissingNode() || itemNode.isNull()) {
            return Collections.emptyList();
        }

        List<TourApiLocationResponse.Item> result = new ArrayList<>();
        if (itemNode.isArray()) {
            for (JsonNode node : itemNode) {
                result.add(objectMapper.treeToValue(node, TourApiLocationResponse.Item.class));
            }
        } else if (itemNode.isObject()) {
            result.add(objectMapper.treeToValue(itemNode, TourApiLocationResponse.Item.class));
        }
        return result;
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
