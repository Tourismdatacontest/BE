package com.tourismdata.contest.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "checkpoints",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "order_no"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkpoint_id")
    private Long checkpointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "name")
    private String name;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // @Lob만 쓰면 Hibernate가 length(기본 255)에 맞춰 MySQL TINYTEXT로 매핑해버려서
    // 조금만 긴 안내문도 잘려나가는 문제가 있었음(실제로 겪음) - columnDefinition으로
    // TEXT를 명시해서 고정.
    @Lob
    @Column(name = "guide_content", columnDefinition = "TEXT")
    private String guideContent;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public Checkpoint(Course course, Integer orderNo, String name, Double latitude,
                       Double longitude, String guideContent, String imageUrl) {
        this.course = course;
        this.orderNo = orderNo;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.guideContent = guideContent;
        this.imageUrl = imageUrl;
    }
}
