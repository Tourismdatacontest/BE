package com.tourismdata.contest.domain.mode.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.mode.dto.ModeSummaryResponse;
import com.tourismdata.contest.domain.mode.service.ModeAdminService;

import lombok.RequiredArgsConstructor;

// 기본 Mode(일반/스토리) 데이터를 심는 내부 운영 도구.
// OpenAPI 명세에 없는 데이터 관리용 엔드포인트라 프론트 연동 대상이 아니다.
@RestController
@RequestMapping("/admin/modes")
@RequiredArgsConstructor
public class ModeAdminController {

    private final ModeAdminService modeAdminService;

    @PostMapping("/seed-default-modes")
    public List<ModeSummaryResponse> seedDefaultModes() {
        return modeAdminService.seedDefaultModes();
    }
}
