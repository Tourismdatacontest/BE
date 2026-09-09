package com.tourismdata.contest.domain.user.controller;

import com.tourismdata.contest.domain.user.dto.KakaoLoginRequest;
import com.tourismdata.contest.domain.user.dto.KakaoLoginResponse;
import com.tourismdata.contest.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/kakao/login")
    public KakaoLoginResponse loginWithKakao(@RequestBody KakaoLoginRequest request) {
        return authService.loginWithKakao(request.code(), request.visitId());
    }
}
