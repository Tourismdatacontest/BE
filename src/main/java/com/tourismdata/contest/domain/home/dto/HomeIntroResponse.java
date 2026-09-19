package com.tourismdata.contest.domain.home.dto;

public record HomeIntroResponse(
        String title,
        String overview,
        String address,
        String homepage,
        String imageUrl,
        String infoCenter,
        String restDate,
        String useTime,
        String parking
) {
}
