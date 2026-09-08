package com.tourismdata.contest.domain.visit.entity;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.user.entity.User;
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

// Kakao OAuth 로그인 확정에 따라 User 엔티티 재도입 (기존 비로그인 방식 결정을 번복).
// user는 JWT 인증 미들웨어가 붙기 전까지는 서비스 계층에서 null로 생성됨 -- 로그인 플로우가
// 완성되면 SecurityContext에서 인증된 사용자를 채우도록 VisitService.createVisit()을 마저 연결해야 한다.
// visitId(순차 증가 PK)는 내부 FK 조인 전용이고, 외부에 노출되는 식별자는 visitUuid다
// (순차 숫자를 그대로 노출하면 다른 사람의 탐방 기록을 순회로 열람/조작할 수 있어 CodeRabbit이 지적함).
// 로그인 전환 이후에도 실제 소유권 검증(로그인된 user와 대조)이 붙기 전까지는 다중 방어 차원에서
// visitUuid를 그대로 유지한다.
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