package com.tourismdata.contest.domain.home.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tourismdata.contest.domain.home.dto.HomeIntroResponse;
import com.tourismdata.contest.external.tourapi.TourSpotInfoClient;

import lombok.extern.slf4j.Slf4j;

// 홈 화면 "남한산성 소개" 텍스트. 한국관광공사 국문 관광정보 서비스(KorService2)의
// detailCommon2/detailIntro2를 실시간 호출해서 내려준다 (DB/파일 저장 없음).
// 캐싱/실패 처리 방식은 HomePhotoService와 동일: 메모리에 TTL 동안만 재사용하고,
// 갱신 실패 시 이전 성공값을 유지하며 1분 뒤 재시도한다.
@Slf4j
@Service
public class HomeIntroService {

    // 남한산성도립공원 [유네스코 세계유산] - contentTypeId 12(관광지)
    private static final String CONTENT_ID = "125449";
    private static final String CONTENT_TYPE_ID = "12";
    private static final Duration FAILURE_RETRY_INTERVAL = Duration.ofMinutes(1);

    private final TourSpotInfoClient client;
    private final Duration cacheTtl;

    private volatile HomeIntroResponse cached;
    private volatile Instant refreshAfter = Instant.MIN;

    public HomeIntroService(TourSpotInfoClient client,
                            @Value("${home.intro.cache-ttl-minutes:360}") long cacheTtlMinutes) {
        this.client = client;
        this.cacheTtl = Duration.ofMinutes(cacheTtlMinutes);
    }

    // 조회 실패 + 캐시도 없으면 empty (컨트롤러가 204 응답 → 프론트는 기존 정적 문구 사용)
    public Optional<HomeIntroResponse> getIntro() {
        if (Instant.now().isBefore(refreshAfter)) {
            return Optional.ofNullable(cached);
        }
        return refresh();
    }

    private synchronized Optional<HomeIntroResponse> refresh() {
        Instant now = Instant.now();
        if (now.isBefore(refreshAfter)) {
            return Optional.ofNullable(cached);
        }

        Optional<HomeIntroResponse> fetched = fetch();
        if (fetched.isEmpty()) {
            refreshAfter = now.plus(FAILURE_RETRY_INTERVAL);
            log.warn("홈 소개 갱신 실패 - 이전 캐시 {}, {}초 후 재시도",
                    cached == null ? "없음" : "유지", FAILURE_RETRY_INTERVAL.toSeconds());
            return Optional.ofNullable(cached);
        }

        cached = fetched.get();
        refreshAfter = now.plus(cacheTtl);
        log.info("홈 소개 갱신 완료 - 다음 갱신 {}분 후", cacheTtl.toMinutes());
        return fetched;
    }

    private Optional<HomeIntroResponse> fetch() {
        try {
            Optional<TourSpotInfoClient.Common> common = client.fetchCommon(CONTENT_ID);
            if (common.isEmpty()) {
                return Optional.empty();
            }
            // 이용정보(문의처/휴무일 등)는 부가정보라 실패해도 소개 본문은 내려준다.
            TourSpotInfoClient.Intro intro = null;
            try {
                intro = client.fetchIntro(CONTENT_ID, CONTENT_TYPE_ID).orElse(null);
            } catch (Exception e) {
                log.warn("detailIntro2 호출 실패(본문만 제공): {}", e.toString());
            }

            TourSpotInfoClient.Common c = common.get();
            return Optional.of(new HomeIntroResponse(
                    c.title(), c.overview(), c.addr1(), c.homepage(), c.firstimage(),
                    intro == null ? null : intro.infocenter(),
                    intro == null ? null : intro.restdate(),
                    intro == null ? null : intro.usetime(),
                    intro == null ? null : intro.parking()));
        } catch (Exception e) {
            log.warn("국문 관광정보 API 호출 실패: {}", e.toString());
            return Optional.empty();
        }
    }
}
