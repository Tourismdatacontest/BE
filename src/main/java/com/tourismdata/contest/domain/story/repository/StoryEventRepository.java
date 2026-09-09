package com.tourismdata.contest.domain.story.repository;

import com.tourismdata.contest.domain.story.entity.StoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryEventRepository extends JpaRepository<StoryEvent, Long> {

    Optional<StoryEvent> findFirstByCheckpoint_CheckpointId(Long checkpointId);

    List<StoryEvent> findByCheckpoint_Course_CourseId(Long courseId);
}
