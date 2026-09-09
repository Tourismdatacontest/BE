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

// 하이브리드 로그인(팀 결정, 2026-09 업데이트): 기본은 비로그인(게스트)으로 시작.
// 스토리 모드 보상 획득 시점에 사용자가 선택적으로 카카오 로그인하면 user가 채워짐
// (AuthService.loginWithKakao에서 연결). user가 null이면 여전히 게스트 방문 기록.
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
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mode_id")
    private Mode mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_checkpoint_id")
    private Checkpoint currentCheckpoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    /** 스토리 모드 보상 획득 시점에 카카오 로그인하면 이 방문 세션을 계정에 연결. */
    public void linkUser(User user) {
        this.user = user;
    }
}
