package com.tourismdata.contest.domain.home.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.home.dto.HomePhotoResponse;
import com.tourismdata.contest.domain.home.service.HomePhotoService;

import lombok.RequiredArgsConstructor;

// GET /home/photos
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomePhotoService homePhotoService;

    @GetMapping("/home/photos")
    public List<HomePhotoResponse> getHomePhotos() {
        return homePhotoService.getBannerPhotos();
    }
}
