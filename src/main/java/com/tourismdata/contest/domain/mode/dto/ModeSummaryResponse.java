package com.tourismdata.contest.domain.mode.dto;

import com.tourismdata.contest.domain.mode.entity.Mode;

public record ModeSummaryResponse(
        Long modeId,
        String name
) {

    public static ModeSummaryResponse from(Mode mode) {
        return new ModeSummaryResponse(mode.getModeId(), mode.getName());
    }
}
