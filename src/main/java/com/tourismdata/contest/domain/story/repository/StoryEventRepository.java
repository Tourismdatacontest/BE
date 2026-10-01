package com.tourismdata.contest.domain.story.repository;

import com.tourismdata.contest.domain.story.entity.StoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryEventRepository extends JpaRepository<StoryEvent, Long> {

    Optional<StoryEvent> findFirstByCheckpoint_CheckpointId(Long checkpointId);

    List<StoryEvent> findByCheckpoint_Course_CourseId(Long courseId);

    // 체크포인트 삭제(관리자 도구) 전, 그 체크포인트에 달린 스토리 이벤트(및 조인 테이블)를
    // 함께 지우기 위함. 개별 엔티티를 조회해 지우므로 story_event_ingredients 연관도 함께 정리된다.
    void deleteByCheckpoint_CheckpointId(Long checkpointId);
}
