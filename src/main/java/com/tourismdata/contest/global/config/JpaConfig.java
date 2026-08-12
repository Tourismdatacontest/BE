package com.tourismdata.contest.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// JPA Auditing 설정 (BaseTimeEntity의 @CreatedDate 자동 채움용).
// MySQL 사용 확정에 따라 Hibernate Spatial 설정은 불필요 (GPS 근접 판정은 GeoUtils에서 Java로 계산).
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
