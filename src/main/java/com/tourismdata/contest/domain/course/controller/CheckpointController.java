package com.tourismdata.contest.domain.course.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourismdata.contest.domain.course.dto.CheckpointPhotoResponse;
import com.tourismdata.contest.domain.course.service.CheckpointPhotoService;

import lombok.RequiredArgsConstructor;

// GET /checkpoints/{checkpointId}/photos
@RestController
@RequestMapping("/checkpoints")
@RequiredArgsConstructor
public class CheckpointController {

    private final CheckpointPhotoService checkpointPhotoService;

    @GetMapping("/{checkpointId}/photos")
    public List<CheckpointPhotoResponse> getPhotos(@PathVariable Long checkpointId) {
        return checkpointPhotoService.getPhotos(checkpointId);
    }
}
