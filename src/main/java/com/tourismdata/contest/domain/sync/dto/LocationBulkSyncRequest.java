package com.tourismdata.contest.domain.sync.dto;

import com.tourismdata.contest.domain.visit.dto.LocationUpdateRequest;

import java.util.List;

public record LocationBulkSyncRequest(
    List<LocationUpdateRequest> locations
) {
}
