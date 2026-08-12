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
