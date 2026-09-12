package com.tourismdata.contest.domain.course.entity;

import com.tourismdata.contest.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "title")
    private String title;

    // @Lob만 쓰면 Hibernate가 length(기본 255)에 맞춰 MySQL TINYTEXT로 매핑해버려서
    // 조금만 긴 설명도 잘려나가는 문제가 있었음(실제로 Checkpoint.guideContent에서
    // 겪음) - columnDefinition으로 TEXT를 명시해서 고정.
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "distance_m")
    private Integer distanceM;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @Builder
    public Course(String title, String description, String thumbnailUrl,
                  Integer distanceM, Integer estimatedMinutes, Difficulty difficulty) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.distanceM = distanceM;
        this.estimatedMinutes = estimatedMinutes;
        this.difficulty = difficulty;
    }
}
