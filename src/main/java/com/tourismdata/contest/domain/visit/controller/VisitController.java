package com.tourismdata.contest.domain.visit.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.visit.dto.VisitCreateRequest;
import com.tourismdata.contest.domain.visit.dto.VisitResponse;
import com.tourismdata.contest.domain.visit.dto.VisitResultResponse;
import com.tourismdata.contest.domain.visit.service.VisitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// POST /visits, GET /visits/{visitId}, POST /visits/{visitId}/complete, GET /visits/{visitId}/result
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponse createVisit(@Valid @RequestBody VisitCreateRequest request) {
        return visitService.createVisit(request);
    }

    @GetMapping("/{visitId}")
    public VisitResponse getVisit(@PathVariable("visitId") UUID visitUuid) {
        return visitService.getVisit(visitUuid);
    }

    @PostMapping("/{visitId}/complete")
    public VisitResponse completeVisit(@PathVariable("visitId") UUID visitUuid) {
        return visitService.completeVisit(visitUuid);
    }

    @GetMapping("/{visitId}/result")
    public VisitResultResponse getVisitResult(@PathVariable("visitId") UUID visitUuid) {
        return visitService.getVisitResult(visitUuid);
    }
}
