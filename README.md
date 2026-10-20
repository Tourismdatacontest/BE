# 남산뚜 Backend

> 병자호란 47일의 이야기를 GPS로 따라 걷는 남한산성 도보 관광 PWA, **남산뚜**(구 "걷다, 남한산성")의 백엔드 서버입니다.
> 2026 관광데이터 활용 공모전 출품작.

## 주요 기능

| 영역 | 설명 |
| --- | --- |
| 코스 / 체크포인트 | 남한산성 코스 목록, 경로 좌표, 코스별 체크포인트 조회 |
| 탐방 세션 | 탐방 시작·진행·완료·결과 조회, 위치 로그 저장 |
| Story Mode | 체크포인트별 스토리 이벤트, 재료(ingredient) 획득, 진행 상태 |
| Nearby | 한국관광공사 TourAPI를 배치로 수집해 주변 맛집·카페·숙소 제공 |
| Offline Sync | 오프라인 캐싱용 코스 패키지, 오프라인 중 쌓인 위치 로그 일괄 동기화 |
| 인증 | 기본은 게스트(비로그인), 보상 수령 시 카카오 로그인으로 계정 전환 |

## 기술 스택

- **Language / Framework**: Java 21, Spring Boot 4.1, Spring Security, Spring Data JPA (Hibernate)
- **Database**: MySQL, Redis
- **Auth**: Kakao OAuth 2.0 (Authorization Code), JWT
- **External API**: TourAPI(한국관광공사 관광정보서비스)
- **Build / Deploy**: Gradle, Docker(멀티스테이지), Railway
- **Frontend**: React + Vite PWA (별도 레포: `(https://github.com/Tourismdatacontest/FE)`)

## 프로젝트 구조

feature 기반 패키지 구조입니다. (`com.tourismdata.contest`)

```
src/main/java/com/tourismdata/contest/
├── course / visit / ...        # 코스, 체크포인트, 탐방 세션, 위치 트래킹
├── story/                      # Story Mode
├── nearby/                     # 주변 정보 (엔티티, 조회 서비스, ETL 동기화 서비스)
├── sync/                       # 오프라인 동기화
├── auth/ user/                 # 카카오 로그인, JWT, 사용자
├── external/
│   ├── tourapi/                # TourAPI 클라이언트, 카테고리 매퍼
│   └── kakaologin/             # 카카오 토큰 교환·프로필 조회
├── batch/                      # @Scheduled 배치 잡 (Nearby ETL)
└── global/                     # SecurityConfig(CORS 포함), 예외 처리, GeoUtils 등
```

> 패키지명은 실제 코드와 맞춰 확인해 주세요.

## 시작하기 (로컬)

### 요구 사항

- JDK 21
- MySQL (로컬 DB 생성 후 `application-local.yml`에 접속 정보 설정)
- Redis (선택: 사용하는 기능이 있을 때)

### 실행

```bash
# 1) 환경변수 설정 (아래 표 참고)
export TOURAPI_SERVICE_KEY=...
export JWT_SECRET=...

# 2) 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 환경변수

비밀값은 코드·커밋·채팅에 남기지 말고 환경변수로만 주입합니다.

| 변수 | 설명 |
| --- | --- |
| `TOURAPI_SERVICE_KEY` | TourAPI 서비스 키 |
| `JWT_SECRET` | JWT 서명 키 |
| `KAKAO_LOGIN_REDIRECT_URI` | 카카오 로그인 Redirect URI. **프론트가 인가 요청에 보내는 `redirect_uri`와 정확히 같아야 함** |
| `ADMIN_API_ENABLED` | `true`일 때만 `/admin/**` 활성화 (기본 `false`) |
| 카카오 클라이언트 키 / DB / Redis 접속 정보 | `application-*.yml`의 placeholder 이름을 따라 설정 |

## API 개요

API 명세(Swagger)는 `<명세 문서 링크>`에 있습니다. 주요 엔드포인트는 다음과 같습니다.

| 영역 | 엔드포인트 |
| --- | --- |
| Course | `GET /courses`, `GET /courses/{courseId}`, `GET /courses/{courseId}/route`, `GET /courses/{courseId}/checkpoints` |
| Visit | `POST /visits`, `GET /visits/{visitId}`, `POST /visits/{visitId}/complete`, `GET /visits/{visitId}/result`, `GET /visits?userId=` |
| Location | `POST /visits/{visitId}/location`, `GET /checkpoints/{checkpointId}` |
| Story | `GET /visits/{visitId}/story/intro`, `GET /visits/{visitId}/story/checkpoints/{checkpointId}`, `POST /visits/{visitId}/story/ingredients`, `GET /visits/{visitId}/story/progress` |
| Nearby | `GET /nearby?courseId=&type=` (`RESTAURANT`, `CAFE`, `LODGING`, `PHOTO_SPOT`) |
| Sync | `GET /courses/{courseId}/offline-package`, `POST /sync/visits/{visitId}/locations` |
| Auth | `POST /auth/kakao/login` |

오류 응답은 `{ "success": false, "data": null, "code": "...", "message": "..." }` 형태입니다.

> `courseId`는 코스 이름(1코스, 2코스…)과 다를 수 있습니다. 실제 ID는 `GET /courses`로 확인하세요.

## 인증 흐름

1. 기본은 게스트입니다. 탐방을 시작하면 `visitId`가 발급되고, 프론트가 로컬에 저장합니다.
2. 보상 수령 시 프론트가 카카오 인가 코드를 받아 `POST /auth/kakao/login`에 `{ "code": "...", "visitIds": [...] }`로 보냅니다.
3. 서버는 카카오에서 토큰을 교환하고 프로필을 조회해 사용자를 생성·조회한 뒤 JWT를 발급합니다.
4. 요청에 담긴 `visitIds`의 탐방 기록을 그 계정에 연결합니다. **자동 소급 연결은 없으며, `visitIds`를 보내야만 연결됩니다.**
5. 연결된 기록은 `GET /visits?userId=`로 조회합니다.

카카오 서버가 일부 자바 HTTP 클라이언트(RestTemplate, JDK HttpClient)의 토큰 교환을 거부해, `KakaoLoginClient`는 **curl 서브프로세스**로 호출합니다. 그래서 실행 환경(Docker 이미지 포함)에 `curl`이 설치되어 있어야 합니다.

## Nearby 데이터 수집 (ETL)

- `@Scheduled` 배치가 TourAPI(`locationBasedList2`)를 호출해 `nearby_place` 테이블에 upsert합니다.
- `GET /nearby`는 외부 API를 호출하지 않고 DB만 조회합니다. (호출 한도·지연 회피)
- 거리 계산은 PostGIS 대신 애플리케이션 계층의 Haversine(`GeoUtils`)을 사용합니다.
- 포토존(`PHOTO_SPOT`)은 ETL 대상이 아니며 직접 등록합니다.

## 배포

- Railway에 Dockerfile(JDK 빌드 → JRE 실행, curl 설치)로 배포합니다. MySQL, Redis는 Railway 플러그인을 사용합니다.
- CORS는 프론트 도메인과 localhost만 허용합니다. (`SecurityConfig`)
- 환경변수는 Railway Variables에서 관리합니다.

## 운영 메모

운영 데이터를 바꾸는 관리자 API(예: 체크포인트 개편 반영)는 평소에 꺼 둡니다.

1. Railway Variables에 `ADMIN_API_ENABLED=true` 추가 후 재배포
2. 해당 `POST /admin/**` 엔드포인트를 한 번 호출 (멱등 처리 여부는 각 API 설명 확인)
3. 결과를 `GET /courses/{courseId}/checkpoints` 등으로 확인
4. `ADMIN_API_ENABLED`를 삭제하거나 `false`로 되돌린 뒤 재배포

## 팀

| 이름 | 역할 |
| --- | --- |
| 윤정현 (Samoyed) | Backend — Story Mode, Nearby(ETL), Offline Sync, 카카오 인증, 배포·운영 |
| 신준호 | Backend — Course, Checkpoint, Visit, Location Tracking |
| 안혜선 | Frontend |
| 주호연 | 기획 |
