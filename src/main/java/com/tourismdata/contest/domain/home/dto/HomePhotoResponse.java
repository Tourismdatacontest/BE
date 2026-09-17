package com.tourismdata.contest.domain.home.dto;

import com.tourismdata.contest.external.tourapi.PhotoGalleryClient;

public record HomePhotoResponse(
        String title,
        String imageUrl
) {

    public static HomePhotoResponse from(PhotoGalleryClient.GalleryItem item) {
        return new HomePhotoResponse(item.galTitle(), item.galWebImageUrl());
    }
}
