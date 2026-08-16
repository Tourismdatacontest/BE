package com.tourismdata.contest.domain.mode.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.mode.dto.ModeDetailResponse;
import com.tourismdata.contest.domain.mode.dto.ModeSummaryResponse;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.mode.repository.ModeRepository;
import com.tourismdata.contest.global.exception.CustomException;
import com.tourismdata.contest.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModeService {

    private final ModeRepository modeRepository;

    public List<ModeSummaryResponse> getModes() {
        return modeRepository.findAll().stream()
                .map(ModeSummaryResponse::from)
                .toList();
    }

    public ModeDetailResponse getModeDetail(Long modeId) {
        Mode mode = modeRepository.findById(modeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODE_NOT_FOUND));
        return ModeDetailResponse.from(mode);
    }
}
