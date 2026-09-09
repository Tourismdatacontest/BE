package com.tourismdata.contest.domain.nearby.controller;

import com.tourismdata.contest.domain.nearby.dto.NearbyPlaceResponse;
import com.tourismdata.contest.domain.nearby.entity.PlaceType;
import com.tourismdata.contest.domain.nearby.service.NearbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NearbyController {

    private final NearbyService nearbyService;

    @GetMapping("/nearby")
    public List<NearbyPlaceResponse> getNearbyPlaces(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) PlaceType type
    ) {
        return nearbyService.getNearbyPlaces(courseId, type);
    }
}
