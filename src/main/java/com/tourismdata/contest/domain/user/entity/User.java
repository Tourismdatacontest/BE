package com.tourismdata.contest.domain.user.entity;

import com.tourismdata.contest.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 카카오 OAuth 기반 로그인 사용자. (팀 결정: 비로그인 -> 로그인 방식으로 전환)
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 카카오 사용자 고유 식별자(sub). 최초 로그인 시 발급받아 이후 재로그인 매칭에 사용.
    @Column(name = "kakao_subject", nullable = false, unique = true, updatable = false)
    private String kakaoSubject;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Builder
    public User(String kakaoSubject, String nickname, String profileImageUrl) {
        this.kakaoSubject = kakaoSubject;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
