package com.tourismdata.contest.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_points",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "sequence"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_point_id")
    private Long routePointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Builder
    public RoutePoint(Course course, Integer sequence, Double latitude, Double longitude) {
        this.course = course;
        this.sequence = sequence;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
