package com.tourismdata.contest.domain.visit.entity;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.mode.entity.Mode;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비로그인 방식(팀 결정, Case A): 로그인 폼 없음, User 엔티티 없음.
// 클라이언트가 visitUuid를 localStorage에 저장해서 식별하며, 기기 변경 시 기록은 유지되지 않음.
// visitId(순차 증가 PK)는 내부 FK 조인 전용이고, 외부에 노출되는 식별자는 visitUuid다
// (순차 숫자를 그대로 노출하면 다른 사람의 탐방 기록을 순회로 열람/조작할 수 있어 CodeRabbit이 지적함).
@Entity
@Table(name = "visits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @Column(name = "visit_uuid", nullable = false, unique = true, updatable = false)
    private UUID visitUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mode_id")
    private Mode mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_checkpoint_id")
    private Checkpoint currentCheckpoint;

    @Column(name = "visited_checkpoint_count")
    private Integer visitedCheckpointCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VisitStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Lob
    @Column(name = "result_summary")
    private String resultSummary;

    @Builder
    public Visit(Course course, Mode mode) {
        this.course = course;
        this.mode = mode;
        this.visitUuid = UUID.randomUUID();
        this.visitedCheckpointCount = 0;
        this.status = VisitStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void moveTo(Checkpoint checkpoint) {
        this.currentCheckpoint = checkpoint;
        this.visitedCheckpointCount++;
    }

    public void complete(String resultSummary) {
        this.status = VisitStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.resultSummary = resultSummary;
    }
}