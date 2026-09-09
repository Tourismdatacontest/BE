package com.tourismdata.contest.domain.sync.controller;

import com.tourismdata.contest.domain.sync.dto.LocationBulkSyncRequest;
import com.tourismdata.contest.domain.sync.service.SyncService;
import com.tourismdata.contest.domain.visit.dto.VisitLocationLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/sync/visits/{visitId}/locations")
    public List<VisitLocationLogResponse> syncLocations(
        @PathVariable Long visitId,
        @RequestBody LocationBulkSyncRequest request
    ) {
        return syncService.syncLocations(visitId, request);
    }
}
