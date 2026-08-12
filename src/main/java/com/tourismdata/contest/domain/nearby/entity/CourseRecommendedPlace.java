package com.tourismdata.contest.domain.nearby.entity;

import com.tourismdata.contest.domain.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

// TourAPI contentId 참조용 큐레이션 테이블. name/address/좌표/이미지는 저장하지 않음(ERD 주석 기준).
@Entity
@Table(name = "course_recommended_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseRecommendedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "external_content_id")
    private String externalContentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PlaceType type;

    @Column(name = "order_no")
    private Integer orderNo;

    @Builder
    public CourseRecommendedPlace(Course course, String externalContentId, PlaceType type, Integer orderNo) {
        this.course = course;
        this.externalContentId = externalContentId;
        this.type = type;
        this.orderNo = orderNo;
    }
}
