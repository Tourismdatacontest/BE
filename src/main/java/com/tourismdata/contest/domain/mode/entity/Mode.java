package com.tourismdata.contest.domain.mode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "modes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mode_id")
    private Long modeId;

    @Column(name = "name", unique = true)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Builder
    public Mode(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
