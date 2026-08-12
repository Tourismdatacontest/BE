package com.tourismdata.contest.domain.visit.entity;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.user.entity.User;
import java.time.LocalDateTime;
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

@Entity
@Table(name = "visits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
    public Visit(User user, Course course, Mode mode) {
        this.user = user;
        this.course = course;
        this.mode = mode;
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
