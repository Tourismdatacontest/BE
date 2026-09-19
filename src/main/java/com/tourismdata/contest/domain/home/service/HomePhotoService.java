package com.tourismdata.contest.domain.home.service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tourismdata.contest.domain.home.dto.HomePhotoResponse;
import com.tourismdata.contest.external.tourapi.PhotoGalleryClient;

import lombok.extern.slf4j.Slf4j;

// 홈 화면 "남한산성 소개" 배너용 사진. 파일/DB에 저장해두지 않고, 한국관광공사 관광사진
// API(PhotoGalleryService1)를 실시간으로 호출해서 그 결과를 내려준다 (공공데이터 활용
// 공모전 요건상 실제 Open API 연동이어야 해서, 관리자 도구로 시딩해서 DB/파일에 저장해두는
// 방식은 쓰지 않았다).
//
// "남한산성" 키워드로만 검색하면 안내판/클로즈업 사진 등도 섞여 나와서, 팀이 실제로
// 하나씩 열어보고 고른 galContentId만 걸러서 노출한다. 이 필터는 "어떤 걸 보여줄지"
// 정하는 목록일 뿐이고, 제목/이미지 URL 등 실제 데이터는 API 응답에서 그대로 가져온다.
//
// 캐싱: 공공데이터포털 개발계정은 일일 호출 한도(1,000건)가 있어서 홈 화면이 열릴 때마다
// API를 부르면 접속자가 조금만 늘어도 한도가 찬다. 그래서 응답을 메모리에 잠깐(기본 30분)
// 재사용한다. 저장소에 영구 저장하는 게 아니라 TTL이 지나면 API를 다시 호출해서 갱신하는
// 단순 메모리 캐시라, "실시간 Open API 연동" 구조는 그대로 유지된다.
@Slf4j
@Service
public class HomePhotoService {

    private static final List<String> CURATED_CONTENT_IDS = List.of(
            "1204940", "1414186", "1984800", "1984809", "2540318",
            "2569314", "2569332", "2569336", "2569337", "2909135"
    );

    private static final String SEARCH_KEYWORD = "남한산성";
    // "남한산성" 검색 결과가 현재 208건인데, numOfRows를 딱 200으로 주면(정확한 이유는
    // 불명이지만) 일부 항목이 누락되는 걸 실제로 겪었다 - 여유 있게 500으로 요청해서
    // 총량이 늘어나도 안전하게 전부 받도록 함.
    private static final int SEARCH_ROWS = 500;

    // 갱신 실패(장애/한도 초과 등) 후 재시도까지 기다리는 시간. 실패 상태에서 요청마다 API를
    // 다시 때리면 한도 초과 상황을 오히려 악화시키므로 짧게라도 간격을 둔다.
    private static final Duration FAILURE_RETRY_INTERVAL = Duration.ofMinutes(1);

    private final PhotoGalleryClient photoGalleryClient;
    private final Duration cacheTtl;

    // 마지막으로 "성공한" 결과. 갱신이 실패해도 이전 성공값이 있으면 만료됐어도 계속 내려준다.
    private volatile List<HomePhotoResponse> cachedPhotos;
    private volatile Instant refreshAfter = Instant.MIN;

    public HomePhotoService(PhotoGalleryClient photoGalleryClient,
                            @Value("${home.photos.cache-ttl-minutes:30}") long cacheTtlMinutes) {
        this.photoGalleryClient = photoGalleryClient;
        this.cacheTtl = Duration.ofMinutes(cacheTtlMinutes);
    }

    public List<HomePhotoResponse> getBannerPhotos() {
        if (Instant.now().isBefore(refreshAfter)) {
            return currentOrEmpty();
        }
        return refresh();
    }

    // 동시에 여러 요청이 만료를 감지해도 API 호출은 한 번만 나가도록 동기화하고 재확인한다.
    private synchronized List<HomePhotoResponse> refresh() {
        Instant now = Instant.now();
        if (now.isBefore(refreshAfter)) {
            return currentOrEmpty();
        }

        List<HomePhotoResponse> fetched = fetchCuratedPhotos();
        if (fetched.isEmpty()) {
            // API가 예외를 던지는 경우뿐 아니라, 한도 초과 등으로 에러 본문이 와서 결과가 비는
            // 경우도 실패로 취급한다 (빈 결과를 캐시에 넣어버리면 TTL 동안 사진이 안 나옴).
            refreshAfter = now.plus(FAILURE_RETRY_INTERVAL);
            log.warn("홈 배너 사진 갱신 실패 - 이전 캐시 {}, {}초 후 재시도",
                    cachedPhotos == null ? "없음" : "유지", FAILURE_RETRY_INTERVAL.toSeconds());
            return currentOrEmpty();
        }

        cachedPhotos = fetched;
        refreshAfter = now.plus(cacheTtl);
        log.info("홈 배너 사진 갱신 완료 - {}장, 다음 갱신 {}분 후", fetched.size(), cacheTtl.toMinutes());
        return fetched;
    }

    private List<HomePhotoResponse> currentOrEmpty() {
        List<HomePhotoResponse> current = cachedPhotos;
        return current == null ? List.of() : current;
    }

    private List<HomePhotoResponse> fetchCuratedPhotos() {
        List<PhotoGalleryClient.GalleryItem> items;
        try {
            items = photoGalleryClient.search(SEARCH_KEYWORD, SEARCH_ROWS);
        } catch (Exception e) {
            log.warn("관광사진 API 호출 실패: {}", e.toString());
            return List.of();
        }

        Map<String, PhotoGalleryClient.GalleryItem> byContentId = new LinkedHashMap<>();
        for (PhotoGalleryClient.GalleryItem item : items) {
            if (item.galContentId() != null) {
                byContentId.put(item.galContentId(), item);
            }
        }

        return CURATED_CONTENT_IDS.stream()
                .map(byContentId::get)
                .filter(Objects::nonNull)
                .map(HomePhotoResponse::from)
                .toList();
    }
}
