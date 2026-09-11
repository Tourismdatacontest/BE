package com.tourismdata.contest.domain.story.entity;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// ⚠️ 스토리라인 확정 후 발견: 한 체크포인트에서 재료를 2개 이상 동시에 줄 수 있음
// (예: 1코스 서문 - 대파+양파). 원래 ERD의 단일 ingredient_id FK로는 표현이 안 돼서
// story_event_ingredients 조인 테이블로 다대다 관계로 확장함.
@Entity
@Table(name = "story_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_event_id")
    private Long storyEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_id")
    private Checkpoint checkpoint;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "story_event_ingredients",
        joinColumns = @JoinColumn(name = "story_event_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    @Builder
    public StoryEvent(Checkpoint checkpoint, String content, List<Ingredient> ingredients) {
        this.checkpoint = checkpoint;
        this.content = content;
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
    }
}
