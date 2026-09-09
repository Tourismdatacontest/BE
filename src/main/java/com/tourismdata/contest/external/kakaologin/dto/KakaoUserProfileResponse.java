package com.tourismdata.contest.external.kakaologin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// GET https://kapi.kakao.com/v2/user/me 응답 매핑.
// nickname/profileImageUrl은 kakao_account.profile 안에 중첩되어 있어 편의 메서드로 꺼내옴.
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserProfileResponse(
    Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(Profile profile) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
        String nickname,
        @JsonProperty("profile_image_url") String profileImageUrl
    ) {
    }

    public String nickname() {
        return (kakaoAccount != null && kakaoAccount.profile() != null)
            ? kakaoAccount.profile().nickname()
            : null;
    }

    public String profileImageUrl() {
        return (kakaoAccount != null && kakaoAccount.profile() != null)
            ? kakaoAccount.profile().profileImageUrl()
            : null;
    }
}
