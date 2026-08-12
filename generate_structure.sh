#!/bin/bash
set -e

# 실행 위치: 프로젝트 루트 (~/Desktop/관광데이터BE) 에서 실행하세요.
# 사용법: bash generate_structure.sh

BASE="src/main/java/com/tourismdata/contest"
RES="src/main/resources"

echo "[1/4] 디렉토리 생성 중..."
mkdir -p \
  "$BASE/global/config" \
  "$BASE/global/exception" \
  "$BASE/global/response" \
  "$BASE/global/util" \
  "$BASE/domain/course/controller" \
  "$BASE/domain/course/service" \
  "$BASE/domain/course/repository" \
  "$BASE/domain/course/entity" \
  "$BASE/domain/course/dto" \
  "$BASE/domain/mode/controller" \
  "$BASE/domain/mode/service" \
  "$BASE/domain/mode/repository" \
  "$BASE/domain/mode/entity" \
  "$BASE/domain/mode/dto" \
  "$BASE/domain/visit/controller" \
  "$BASE/domain/visit/service" \
  "$BASE/domain/visit/repository" \
  "$BASE/domain/visit/entity" \
  "$BASE/domain/visit/dto" \
  "$BASE/domain/story/controller" \
  "$BASE/domain/story/service" \
  "$BASE/domain/story/repository" \
  "$BASE/domain/story/entity" \
  "$BASE/domain/story/dto" \
  "$BASE/domain/nearby/controller" \
  "$BASE/domain/nearby/service" \
  "$BASE/domain/nearby/repository" \
  "$BASE/domain/nearby/entity" \
  "$BASE/domain/nearby/dto" \
  "$BASE/domain/sync/controller" \
  "$BASE/domain/sync/service" \
  "$BASE/domain/sync/dto" \
  "$BASE/external/tourapi/dto" \
  "$BASE/external/kakaolocal/dto" \
  "$BASE/batch" \
  "$RES" \
  "src/test/java/com/tourismdata/contest"

echo "[2/4] ContestApplication.java 생성 중..."
cat > "$BASE/ContestApplication.java" << 'EOF'
package com.tourismdata.contest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ContestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContestApplication.class, args);
    }
}
EOF

echo "[3/4] 도메인별 스켈레톤 파일 생성 중..."

# 헬퍼 함수: 클래스 스켈레톤 생성
make_class() {
  local path="$1"; local pkg="$2"; local note="$3"
  local cname
  cname=$(basename "$path" .java)
  {
    echo "package com.tourismdata.contest.${pkg};"
    echo ""
    [ -n "$note" ] && echo "$note"
    echo "public class ${cname} {"
    echo ""
    echo "}"
  } > "$BASE/$path"
}

make_interface() {
  local path="$1"; local pkg="$2"; local note="$3"
  local cname
  cname=$(basename "$path" .java)
  {
    echo "package com.tourismdata.contest.${pkg};"
    echo ""
    [ -n "$note" ] && echo "$note"
    echo "public interface ${cname} {"
    echo ""
    echo "}"
  } > "$BASE/$path"
}

make_enum() {
  local path="$1"; local pkg="$2"; local note="$3"
  local cname
  cname=$(basename "$path" .java)
  {
    echo "package com.tourismdata.contest.${pkg};"
    echo ""
    [ -n "$note" ] && echo "$note"
    echo "public enum ${cname} {"
    echo ""
    echo "}"
  } > "$BASE/$path"
}

make_record() {
  local path="$1"; local pkg="$2"; local note="$3"
  local cname
  cname=$(basename "$path" .java)
  {
    echo "package com.tourismdata.contest.${pkg};"
    echo ""
    [ -n "$note" ] && echo "$note"
    echo "public record ${cname}() {"
    echo ""
    echo "}"
  } > "$BASE/$path"
}

# ---- global ----
make_class   "global/config/JpaConfig.java"            "global.config"    "// Hibernate Spatial / JPA Auditing 설정"
make_class   "global/config/RedisConfig.java"           "global.config"    "// Redis 캐시 설정"
make_class   "global/config/SchedulerConfig.java"       "global.config"    "// @EnableScheduling 관련 스레드풀 설정 (TourAPI/Kakao ETL)"
make_class   "global/config/SwaggerConfig.java"         "global.config"    "// OpenAPI(Swagger) 문서 설정"
make_class   "global/config/SecurityConfig.java"        "global.config"    "// TODO: 인증 방식 확정 후 구현 (기기 간 기록 공유 필요 여부에 따라 결정)"
make_class   "global/exception/GlobalExceptionHandler.java" "global.exception" "// @RestControllerAdvice 전역 예외 처리"
make_class   "global/exception/CustomException.java"    "global.exception" "// 공통 커스텀 예외"
make_enum    "global/exception/ErrorCode.java"          "global.exception" "// 에러 코드 enum"
make_class   "global/response/ApiResponse.java"         "global.response"  "// 공통 API 응답 래퍼"
make_class   "global/util/GeoUtils.java"                "global.util"      "// PostGIS ST_DWithin 관련 GPS 거리 계산 헬퍼"

# ---- domain/course ----
make_class     "domain/course/controller/CourseController.java" "domain.course.controller" "// GET /courses, /courses/{courseId}, /courses/{courseId}/route, /courses/{courseId}/checkpoints"
make_class     "domain/course/service/CourseService.java"       "domain.course.service"     ""
make_interface "domain/course/repository/CourseRepository.java" "domain.course.repository"  ""
make_interface "domain/course/repository/RoutePointRepository.java" "domain.course.repository" ""
make_interface "domain/course/repository/CheckpointRepository.java" "domain.course.repository" ""
make_class     "domain/course/entity/Course.java"      "domain.course.entity" ""
make_class     "domain/course/entity/RoutePoint.java"  "domain.course.entity" ""
make_class     "domain/course/entity/Checkpoint.java"  "domain.course.entity" ""
make_record    "domain/course/dto/CourseSummaryResponse.java" "domain.course.dto" ""
make_record    "domain/course/dto/CourseDetailResponse.java"  "domain.course.dto" ""
make_record    "domain/course/dto/RoutePointResponse.java"    "domain.course.dto" ""
make_record    "domain/course/dto/CheckpointResponse.java"    "domain.course.dto" ""

# ---- domain/mode ----
make_class     "domain/mode/controller/ModeController.java" "domain.mode.controller" "// GET /modes, /modes/{modeId}"
make_class     "domain/mode/service/ModeService.java"       "domain.mode.service"     ""
make_interface "domain/mode/repository/ModeRepository.java" "domain.mode.repository"  ""
make_class     "domain/mode/entity/Mode.java"                "domain.mode.entity"      ""
make_record    "domain/mode/dto/ModeSummaryResponse.java"    "domain.mode.dto"         ""
make_record    "domain/mode/dto/ModeDetailResponse.java"     "domain.mode.dto"         ""

# ---- domain/visit (담당 A) ----
make_class     "domain/visit/controller/VisitController.java"   "domain.visit.controller" "// POST /visits, GET /visits/{visitId}, POST /visits/{visitId}/complete, GET /visits/{visitId}/result"
make_class     "domain/visit/controller/LocationController.java" "domain.visit.controller" "// POST /visits/{visitId}/location, GET /checkpoints/{checkpointId}"
make_class     "domain/visit/service/VisitService.java"          "domain.visit.service"    ""
make_class     "domain/visit/service/LocationTrackingService.java" "domain.visit.service"  "// ST_DWithin 기반 nearbyCheckpoint 판정 로직 (Developer B 연동 지점)"
make_interface "domain/visit/repository/VisitRepository.java"    "domain.visit.repository" ""
make_interface "domain/visit/repository/VisitLocationLogRepository.java" "domain.visit.repository" ""
make_class     "domain/visit/entity/Visit.java"                  "domain.visit.entity"     ""
make_class     "domain/visit/entity/VisitLocationLog.java"       "domain.visit.entity"     ""
make_record    "domain/visit/dto/VisitCreateRequest.java"        "domain.visit.dto"        ""
make_record    "domain/visit/dto/VisitResponse.java"             "domain.visit.dto"        ""
make_record    "domain/visit/dto/VisitResultResponse.java"       "domain.visit.dto"        ""
make_record    "domain/visit/dto/LocationUpdateRequest.java"     "domain.visit.dto"        ""
make_record    "domain/visit/dto/NearbyCheckpointResponse.java"  "domain.visit.dto"        "// ⚠️ Developer A 생성 → Developer B(Story) 소비. 스키마 조기 확정 필요"

# ---- domain/story (담당 B) ----
make_class     "domain/story/controller/StoryController.java" "domain.story.controller" "// GET /visits/{visitId}/story/intro, /story/checkpoints/{checkpointId}, /story/progress, POST /story/ingredients"
make_class     "domain/story/service/StoryService.java"       "domain.story.service"     ""
make_interface "domain/story/repository/StoryEventRepository.java" "domain.story.repository" ""
make_interface "domain/story/repository/IngredientRepository.java" "domain.story.repository" ""
make_interface "domain/story/repository/VisitIngredientRepository.java" "domain.story.repository" ""
make_class     "domain/story/entity/StoryEvent.java"      "domain.story.entity" ""
make_class     "domain/story/entity/Ingredient.java"      "domain.story.entity" ""
make_class     "domain/story/entity/VisitIngredient.java" "domain.story.entity" ""
make_record    "domain/story/dto/StoryIntroResponse.java"       "domain.story.dto" ""
make_record    "domain/story/dto/StoryEventResponse.java"       "domain.story.dto" ""
make_record    "domain/story/dto/IngredientAcquireRequest.java" "domain.story.dto" ""
make_record    "domain/story/dto/StoryProgressResponse.java"    "domain.story.dto" ""

# ---- domain/nearby (담당 B) ----
make_class     "domain/nearby/controller/NearbyController.java" "domain.nearby.controller" "// GET /nearby?courseId=&type="
make_class     "domain/nearby/service/NearbyService.java"       "domain.nearby.service"    "// TODO: ETL 방식이면 NearbyPlaceRepository 조회 / 실시간이면 TourApiClient 직접 호출 (팀 결정 확인 필요)"
make_interface "domain/nearby/repository/NearbyPlaceRepository.java" "domain.nearby.repository" ""
make_interface "domain/nearby/repository/CourseRecommendedPlaceRepository.java" "domain.nearby.repository" ""
make_class     "domain/nearby/entity/NearbyPlace.java"           "domain.nearby.entity" "// source 컬럼: TOURAPI | KAKAO | CURATED"
make_class     "domain/nearby/entity/CourseRecommendedPlace.java" "domain.nearby.entity" "// external_content_id 큐레이션 테이블"
make_record    "domain/nearby/dto/NearbyPlaceResponse.java"      "domain.nearby.dto" ""

# ---- domain/sync (담당 B) ----
make_class  "domain/sync/controller/SyncController.java"          "domain.sync.controller" "// POST /sync/visits/{visitId}/locations"
make_class  "domain/sync/controller/OfflinePackageController.java" "domain.sync.controller" "// GET /courses/{courseId}/offline-package"
make_class  "domain/sync/service/SyncService.java"           "domain.sync.service" ""
make_class  "domain/sync/service/OfflinePackageService.java" "domain.sync.service" ""
make_record "domain/sync/dto/LocationBulkSyncRequest.java"   "domain.sync.dto" ""
make_record "domain/sync/dto/OfflinePackageResponse.java"    "domain.sync.dto" ""

# ---- external ----
make_class  "external/tourapi/TourApiClient.java"        "external.tourapi"      "// 한국관광공사 TourAPI 호출 클라이언트 (ETL 소스)"
make_record "external/tourapi/dto/TourApiPlaceDto.java"  "external.tourapi.dto"  ""
make_class  "external/kakaolocal/KakaoLocalClient.java"       "external.kakaolocal"     "// Kakao Local API 호출 클라이언트 (ETL 소스)"
make_record "external/kakaolocal/dto/KakaoLocalPlaceDto.java" "external.kakaolocal.dto" ""

# ---- batch ----
make_class "batch/NearbyPlaceSyncScheduler.java" "batch" "// @Scheduled: TourAPI/Kakao Local -> nearby_place 적재"

echo "[4/4] application.yml 생성 중..."

cat > "$RES/application.yml" << 'EOF'
spring:
  profiles:
    active: local

server:
  port: 8080
EOF

cat > "$RES/application-local.yml" << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tourismdata_contest
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect
    show-sql: true

  data:
    redis:
      host: localhost
      port: 6379

external:
  tourapi:
    base-url: https://apis.data.go.kr/B551011/KorService1
    service-key: ${TOURAPI_SERVICE_KEY:}
  kakao-local:
    base-url: https://dapi.kakao.com/v2/local
    rest-api-key: ${KAKAO_REST_API_KEY:}

logging:
  level:
    com.tourismdata.contest: DEBUG
EOF

cat > "$RES/application-prod.yml" << 'EOF'
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect
    show-sql: false

  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}

external:
  tourapi:
    base-url: https://apis.data.go.kr/B551011/KorService1
    service-key: ${TOURAPI_SERVICE_KEY}
  kakao-local:
    base-url: https://dapi.kakao.com/v2/local
    rest-api-key: ${KAKAO_REST_API_KEY}

logging:
  level:
    com.tourismdata.contest: INFO
EOF

echo ""
echo "완료! 총 $(find "$BASE" -name '*.java' | wc -l | tr -d ' ')개 자바 파일, $(find "$RES" -name '*.yml' | wc -l | tr -d ' ')개 yml 생성됨."
echo "기존 com/Tourismdata(대문자) 패키지의 ContestApplication.java는 중복이니 직접 삭제해주세요."
