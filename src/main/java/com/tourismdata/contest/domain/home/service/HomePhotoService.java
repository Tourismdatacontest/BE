package com.tourismdata.contest.domain.home.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.tourismdata.contest.domain.home.dto.HomePhotoResponse;
import com.tourismdata.contest.external.tourapi.PhotoGalleryClient;

import lombok.RequiredArgsConstructor;

// 홈 화면 "남한산성 소개" 배너용 사진. 파일/DB에 저장해두지 않고, 요청이 올 때마다
// 한국관광공사 관광사진 API(PhotoGalleryService1)를 실시간으로 호출해서 그 결과를
// 그대로 내려준다 (공공데이터 활용 공모전 요건상 실제 Open API 연동이어야 해서,
// 관리자 도구로 한 번 시딩해서 DB/파일에 저장해두는 방식은 쓰지 않았다).
//
// "남한산성" 키워드로만 검색하면 안내판/클로즈업 사진 등도 섞여 나와서, 팀이 실제로
// 하나씩 열어보고 고른 galContentId만 걸러서 노출한다. 이 필터는 "어떤 걸 보여줄지"
// 정하는 목록일 뿐이고, 제목/이미지 URL 등 실제 데이터는 매 요청마다 API에서 그대로
// 가져온다 - 이미지 자체를 저장해두는 게 아니다.
@Service
@RequiredArgsConstructor
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

    private final PhotoGalleryClient photoGalleryClient;

    public List<HomePhotoResponse> getBannerPhotos() {
        List<PhotoGalleryClient.GalleryItem> items = fetchLiveResults();

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

    // TourAPI 장애/일시적 오류로 홈 화면 전체가 깨지면 안 되니, 실패하면 빈 목록으로 대응한다.
    private List<PhotoGalleryClient.GalleryItem> fetchLiveResults() {
        try {
            return photoGalleryClient.search(SEARCH_KEYWORD, SEARCH_ROWS);
        } catch (Exception e) {
            return List.of();
        }
    }
}
