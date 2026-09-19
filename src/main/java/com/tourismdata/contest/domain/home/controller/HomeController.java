package com.tourismdata.contest.domain.home.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.home.dto.HomeIntroResponse;
import com.tourismdata.contest.domain.home.dto.HomePhotoResponse;
import com.tourismdata.contest.domain.home.service.HomeIntroService;
import com.tourismdata.contest.domain.home.service.HomePhotoService;

import lombok.RequiredArgsConstructor;

// GET /home/photos
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomePhotoService homePhotoService;
    private final HomeIntroService homeIntroService;

    @GetMapping("/home/photos")
    public List<HomePhotoResponse> getHomePhotos() {
        return homePhotoService.getBannerPhotos();
    }

    // 데이터를 못 가져오면 204 - 프론트는 기존 정적 소개 문구로 대체
    @GetMapping("/home/intro")
    public ResponseEntity<HomeIntroResponse> getHomeIntro() {
        return homeIntroService.getIntro()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
