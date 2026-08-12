#!/bin/bash
set -e

BASE="src/main/java/com/tourismdata/contest"

echo "[1/2] 신규 디렉토리 생성 중..."
mkdir -p "$BASE/global/entity" "$BASE/domain/user/entity"

echo "[2/2] 엔티티 파일 생성 중..."

# ---- global/entity/BaseTimeEntity.java ----
cat > "$BASE/global/entity/BaseTimeEntity.java" << 'EOF'
package com.tourismdata.contest.global.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// createdAt이 있는 엔티티(User, Course)가 상속. JpaConfig에서 @EnableJpaAuditing 필요.
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
EOF

# ---- domain/user/entity/User.java ----
cat > "$BASE/domain/user/entity/User.java" << 'EOF'
package com.tourismdata.contest.domain.user.entity;

import com.tourismdata.contest.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: 인증 방식 확정 후 email/password 등 인증 관련 필드 추가 검토
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Builder
    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
EOF

# ---- domain/course/entity/Difficulty.java ----
cat > "$BASE/domain/course/entity/Difficulty.java" << 'EOF'
package com.tourismdata.contest.domain.course.entity;

public enum Difficulty {
    EASY, NORMAL, HARD
}
EOF

# ---- domain/course/entity/Course.java ----
cat > "$BASE/domain/course/entity/Course.java" << 'EOF'
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

    @Lob
    @Column(name = "description")
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
EOF

# ---- domain/course/entity/RoutePoint.java ----
cat > "$BASE/domain/course/entity/RoutePoint.java" << 'EOF'
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_points")
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
EOF

# ---- domain/course/entity/Checkpoint.java ----
cat > "$BASE/domain/course/entity/Checkpoint.java" << 'EOF'
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "checkpoints")
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

    @Lob
    @Column(name = "guide_content")
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
EOF

# ---- domain/mode/entity/Mode.java ----
cat > "$BASE/domain/mode/entity/Mode.java" << 'EOF'
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

    @Column(name = "name")
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
EOF

# ---- domain/visit/entity/VisitStatus.java ----
cat > "$BASE/domain/visit/entity/VisitStatus.java" << 'EOF'
package com.tourismdata.contest.domain.visit.entity;

public enum VisitStatus {
    IN_PROGRESS, COMPLETED
}
EOF

# ---- domain/visit/entity/Visit.java ----
cat > "$BASE/domain/visit/entity/Visit.java" << 'EOF'
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
EOF

# ---- domain/visit/entity/VisitLocationLog.java ----
cat > "$BASE/domain/visit/entity/VisitLocationLog.java" << 'EOF'
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
EOF

# ---- domain/story/entity/Ingredient.java ----
cat > "$BASE/domain/story/entity/Ingredient.java" << 'EOF'
package com.tourismdata.contest.domain.story.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public Ingredient(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
EOF

# ---- domain/story/entity/StoryEvent.java ----
cat > "$BASE/domain/story/entity/StoryEvent.java" << 'EOF'
package com.tourismdata.contest.domain.story.entity;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "story_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_event_id")
    private Long storyEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_id")
    private Checkpoint checkpoint;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Builder
    public StoryEvent(Checkpoint checkpoint, String content, Ingredient ingredient) {
        this.checkpoint = checkpoint;
        this.content = content;
        this.ingredient = ingredient;
    }
}
EOF

# ---- domain/story/entity/VisitIngredientId.java ----
cat > "$BASE/domain/story/entity/VisitIngredientId.java" << 'EOF'
package com.tourismdata.contest.domain.story.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitIngredientId implements Serializable {

    private Long visit;
    private Long ingredient;

    public VisitIngredientId(Long visit, Long ingredient) {
        this.visit = visit;
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitIngredientId that)) return false;
        return Objects.equals(visit, that.visit) && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visit, ingredient);
    }
}
EOF

# ---- domain/story/entity/VisitIngredient.java ----
cat > "$BASE/domain/story/entity/VisitIngredient.java" << 'EOF'
package com.tourismdata.contest.domain.story.entity;

import com.tourismdata.contest.domain.visit.entity.Visit;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ERD 복합키(visit_id, ingredient_id) 반영 - @EmbeddedId 사용
@Entity
@Table(name = "visit_ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitIngredient {

    @EmbeddedId
    private VisitIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("visit")
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredient")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @Builder
    public VisitIngredient(Visit visit, Ingredient ingredient) {
        this.visit = visit;
        this.ingredient = ingredient;
        this.id = new VisitIngredientId(visit.getVisitId(), ingredient.getIngredientId());
        this.acquiredAt = LocalDateTime.now();
    }
}
EOF

# ---- domain/nearby/entity/PlaceType.java ----
cat > "$BASE/domain/nearby/entity/PlaceType.java" << 'EOF'
package com.tourismdata.contest.domain.nearby.entity;

public enum PlaceType {
    RESTAURANT, CAFE, LODGING, PHOTO_SPOT
}
EOF

# ---- domain/nearby/entity/CourseRecommendedPlace.java ----
cat > "$BASE/domain/nearby/entity/CourseRecommendedPlace.java" << 'EOF'
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
EOF

# ---- domain/nearby/entity/NearbyPlace.java ----
cat > "$BASE/domain/nearby/entity/NearbyPlace.java" << 'EOF'
package com.tourismdata.contest.domain.nearby.entity;

// ⚠️ 이 테이블은 ERD 문서에 없음. 메모리상 ETL 결정(TourAPI/Kakao Local 사전 적재 + source 컬럼)을
// 기준으로 임의 작성했으므로, ERD 최신화 여부를 팀과 확인 후 필드 확정 필요.
import com.tourismdata.contest.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nearby_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NearbyPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nearby_place_id")
    private Long nearbyPlaceId;

    @Column(name = "external_content_id")
    private String externalContentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PlaceType type;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "image_url")
    private String imageUrl;

    // TOURAPI | KAKAO | CURATED
    @Column(name = "source")
    private String source;

    @Builder
    public NearbyPlace(String externalContentId, PlaceType type, String name, String address,
                        Double latitude, Double longitude, String imageUrl, String source) {
        this.externalContentId = externalContentId;
        this.type = type;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.source = source;
    }
}
EOF

echo ""
echo "완료! 총 $(find "$BASE" -path '*/entity/*.java' | wc -l | tr -d ' ')개 엔티티 관련 파일 생성됨."
echo "build.gradle에 Lombok, Spring Data JPA Auditing(@EnableJpaAuditing) 설정이 필요합니다."
