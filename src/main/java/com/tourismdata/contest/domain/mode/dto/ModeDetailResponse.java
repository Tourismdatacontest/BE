package com.tourismdata.contest.domain.mode.dto;

import com.tourismdata.contest.domain.mode.entity.Mode;

public record ModeDetailResponse(
        Long modeId,
        String name,
        String description
) {

    public static ModeDetailResponse from(Mode mode) {
        return new ModeDetailResponse(mode.getModeId(), mode.getName(), mode.getDescription());
    }
}
