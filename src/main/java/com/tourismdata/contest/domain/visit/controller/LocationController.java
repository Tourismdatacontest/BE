package com.tourismdata.contest.domain.visit.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.course.dto.CheckpointResponse;
import com.tourismdata.contest.domain.visit.dto.LocationUpdateRequest;
import com.tourismdata.contest.domain.visit.dto.VisitLocationLogResponse;
import com.tourismdata.contest.domain.visit.service.LocationTrackingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// POST /visits/{visitId}/location, GET /checkpoints/{checkpointId}
@RestController
@RequiredArgsConstructor
public class LocationController {

    private final LocationTrackingService locationTrackingService;

    @PostMapping("/visits/{visitId}/location")
    public VisitLocationLogResponse updateLocation(@PathVariable("visitId") UUID visitUuid,
                                                     @Valid @RequestBody LocationUpdateRequest request) {
        return locationTrackingService.recordLocation(visitUuid, request);
    }

    @GetMapping("/checkpoints/{checkpointId}")
    public CheckpointResponse getCheckpoint(@PathVariable Long checkpointId) {
        return locationTrackingService.getCheckpointDetail(checkpointId);
    }
}
