package com.tourismdata.contest.domain.course.service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tourismdata.contest.domain.course.dto.CheckpointPhotoResponse;
import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.external.tourapi.PhotoGalleryClient;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

// 체크포인트 상세용 사진. HomePhotoService와 같은 방식: 한국관광공사 관광사진 API를 실시간 호출하고
// (파일/DB 저장 없음), 체크포인트별로 "어떤 사진을 보여줄지"만 galContentId로 골라둔다.
// 같은 장소는 코스마다 다른 체크포인트 행으로 들어가지만 이름이 같아서, 이름으로 사진을 매칭한다.
@Slf4j
@Service
public class CheckpointPhotoService {

    private static final Map<String, List<String>> CURATED_BY_CHECKPOINT_NAME = Map.of(
            // 서문(우익문): 갤러리 #101, #102, #179
            "서문(우익문)", List.of("1204331", "1204343", "2569738")
    );

    private static final String SEARCH_KEYWORD = "남한산성";
    // 검색 결과가 200건을 넘어 numOfRows를 넉넉히 500으로 요청 (HomePhotoService 참고).
    private static final int SEARCH_ROWS = 500;
    private static final Duration FAILURE_RETRY_INTERVAL = Duration.ofMinutes(1);

    private final CheckpointRepository checkpointRepository;
    private final PhotoGalleryClient photoGalleryClient;
    private final Duration cacheTtl;

    private volatile Map<String, PhotoGalleryClient.GalleryItem> cachedGallery;
    private volatile Instant refreshAfter = Instant.MIN;

    public CheckpointPhotoService(CheckpointRepository checkpointRepository,
                                  PhotoGalleryClient photoGalleryClient,
                                  @Value("${home.photos.cache-ttl-minutes:30}") long cacheTtlMinutes) {
        this.checkpointRepository = checkpointRepository;
        this.photoGalleryClient = photoGalleryClient;
        this.cacheTtl = Duration.ofMinutes(cacheTtlMinutes);
    }

    // 지정된 사진이 없는 체크포인트, 또는 API 실패 시에는 빈 배열
    public List<CheckpointPhotoResponse> getPhotos(Long checkpointId) {
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHECKPOINT_NOT_FOUND));

        List<String> contentIds = CURATED_BY_CHECKPOINT_NAME.get(checkpoint.getName());
        if (contentIds == null) {
            return List.of();
        }

        Map<String, PhotoGalleryClient.GalleryItem> gallery = getGallery();
        return contentIds.stream()
                .map(gallery::get)
                .filter(Objects::nonNull)
                .map(item -> new CheckpointPhotoResponse(item.galTitle(), item.galWebImageUrl()))
                .toList();
    }

    private Map<String, PhotoGalleryClient.GalleryItem> getGallery() {
        if (Instant.now().isBefore(refreshAfter)) {
            return currentOrEmpty();
        }
        return refresh();
    }

    private synchronized Map<String, PhotoGalleryClient.GalleryItem> refresh() {
        Instant now = Instant.now();
        if (now.isBefore(refreshAfter)) {
            return currentOrEmpty();
        }

        Map<String, PhotoGalleryClient.GalleryItem> fetched = fetchGallery();
        if (fetched.isEmpty()) {
            refreshAfter = now.plus(FAILURE_RETRY_INTERVAL);
            log.warn("체크포인트 사진 갱신 실패 - 이전 캐시 {}, {}초 후 재시도",
                    cachedGallery == null ? "없음" : "유지", FAILURE_RETRY_INTERVAL.toSeconds());
            return currentOrEmpty();
        }

        cachedGallery = fetched;
        refreshAfter = now.plus(cacheTtl);
        return fetched;
    }

    private Map<String, PhotoGalleryClient.GalleryItem> currentOrEmpty() {
        Map<String, PhotoGalleryClient.GalleryItem> current = cachedGallery;
        return current == null ? Map.of() : current;
    }

    private Map<String, PhotoGalleryClient.GalleryItem> fetchGallery() {
        try {
            Map<String, PhotoGalleryClient.GalleryItem> byContentId = new LinkedHashMap<>();
            for (PhotoGalleryClient.GalleryItem item : photoGalleryClient.search(SEARCH_KEYWORD, SEARCH_ROWS)) {
                if (item.galContentId() != null) {
                    byContentId.put(item.galContentId(), item);
                }
            }
            return byContentId;
        } catch (Exception e) {
            log.warn("관광사진 API 호출 실패: {}", e.toString());
            return Map.of();
        }
    }
}
