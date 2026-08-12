package com.tourismdata.contest.domain.visit.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visit_location_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitLocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "synced")
    private Boolean synced;

    @Builder
    public VisitLocationLog(Visit visit, Double latitude, Double longitude, LocalDateTime recordedAt) {
        this.visit = visit;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
        this.synced = false;
    }

    public void markSynced() {
        this.synced = true;
    }
}
