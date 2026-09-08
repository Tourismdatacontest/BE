-- visit_uuid_migration.sql
--
-- 목적: CodeRabbit이 지적한 visitId(순차 증가) 열거 공격 문제 대응 (팀 결정: 옵션 A).
--       RoutePoint/Checkpoint에 (course_id, sequence)/(course_id, order_no) 유니크 제약 추가,
--       Visit에 visit_uuid(외부 공개용 식별자) 추가.
--
-- 상태: 아직 팀 공용/배포 DB에는 적용 전. 각자 환경에서 실행 전에 아래 "1) 현재 상태 확인"부터
--       먼저 돌려서 이미 적용된 부분이 있는지 확인할 것 (ddl-auto: validate라 자동으로는 안 걸림).
--
-- 실행 전 DB 백업 권장 (mysqldump).

USE tourismdata_contest;

-- ============================================================
-- 1) 현재 상태 확인 (먼저 실행해서 이미 적용된 부분 있는지 확인)
-- ============================================================
-- SHOW CREATE TABLE route_points;
-- SHOW CREATE TABLE checkpoints;
-- SHOW CREATE TABLE visits;

-- ============================================================
-- 2) RoutePoint / Checkpoint 유니크 제약
-- ============================================================
ALTER TABLE route_points
  ADD CONSTRAINT uq_route_points_course_sequence UNIQUE (course_id, sequence);

ALTER TABLE checkpoints
  ADD CONSTRAINT uq_checkpoints_course_order_no UNIQUE (course_id, order_no);

-- ============================================================
-- 3) Visit.visit_uuid — 컬럼 신규 추가 + 기존 행 백필 + NOT NULL + 유니크 제약
--    (UUID_TO_BIN이 Hibernate의 BINARY(16) 인코딩과 바이트 단위로 정확히 일치하는 것을
--     로컬에서 직접 검증함: BIN_TO_UUID(visit_uuid)와 UUID_TO_BIN(그 문자열)의 HEX가 동일)
-- ============================================================
ALTER TABLE visits ADD COLUMN visit_uuid BINARY(16) NULL;

UPDATE visits SET visit_uuid = UUID_TO_BIN(UUID()) WHERE visit_uuid IS NULL;

ALTER TABLE visits MODIFY COLUMN visit_uuid BINARY(16) NOT NULL;

ALTER TABLE visits ADD CONSTRAINT uq_visits_visit_uuid UNIQUE (visit_uuid);

-- ============================================================
-- 4) 검증 (UUID가 정상 형식으로 보이면 성공)
-- ============================================================
-- SELECT visit_id, BIN_TO_UUID(visit_uuid) FROM visits LIMIT 5;
