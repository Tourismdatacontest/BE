package com.tourismdata.contest.domain.mode.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.mode.dto.ModeDetailResponse;
import com.tourismdata.contest.domain.mode.dto.ModeSummaryResponse;
import com.tourismdata.contest.domain.mode.service.ModeService;

import lombok.RequiredArgsConstructor;

// GET /modes, /modes/{modeId}
@RestController
@RequestMapping("/modes")
@RequiredArgsConstructor
public class ModeController {

    private final ModeService modeService;

    @GetMapping
    public List<ModeSummaryResponse> getModes() {
        return modeService.getModes();
    }

    @GetMapping("/{modeId}")
    public ModeDetailResponse getModeDetail(@PathVariable Long modeId) {
        return modeService.getModeDetail(modeId);
    }
}
