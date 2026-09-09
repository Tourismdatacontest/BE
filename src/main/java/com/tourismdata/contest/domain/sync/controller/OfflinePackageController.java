package com.tourismdata.contest.domain.sync.controller;

import com.tourismdata.contest.domain.sync.dto.OfflinePackageResponse;
import com.tourismdata.contest.domain.sync.service.OfflinePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OfflinePackageController {

    private final OfflinePackageService offlinePackageService;

    @GetMapping("/courses/{courseId}/offline-package")
    public OfflinePackageResponse getOfflinePackage(@PathVariable Long courseId) {
        return offlinePackageService.getOfflinePackage(courseId);
    }
}
