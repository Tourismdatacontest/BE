-- 운영 DB에는 자동 반영되지 않는 수동 마이그레이션 스크립트.
--
-- 배경: application-prod.yml은 ddl-auto: validate라 스키마를 자동으로 바꾸지 않고,
-- 이 프로젝트에는 Flyway/Liquibase 같은 마이그레이션 도구가 아직 없다. 로컬(ddl-auto:
-- update)에서는 Checkpoint.guideContent / Course.description을 @Lob만 쓰면 Hibernate가
-- length(기본 255) 기준으로 MySQL TINYTEXT로 매핑해서 조금만 긴 텍스트도 잘려나가는
-- 문제가 있어 엔티티에 columnDefinition="TEXT"를 추가했다(PR #10). 운영 DB가 이미
-- TINYTEXT로 생성돼 있다면 이 스크립트를 배포 전에 한 번 수동으로 실행해야 한다.
--
-- 실행 대상: checkpoints.guide_content, courses.description
-- 실행 시점: 이 변경이 담긴 배포 직전 (또는 직후, 애플리케이션이 해당 컬럼에 긴 텍스트를
-- 쓰기 전이면 순서 무관)
-- 실행 방법: 운영 DB에 직접 접속해서 아래 구문을 그대로 실행 (또는 팀에서 쓰는 DB 관리
-- 콘솔에 붙여넣기)
--
-- ⚠️ 이 스크립트를 실행한 뒤에는 다시 실행해도 안전하다(TEXT -> TEXT는 no-op).
-- ⚠️ 팀 전체에 반복될 문제라 정식 마이그레이션 도구(Flyway 등) 도입을 팀에서 논의해볼
-- 필요가 있다 - 이 스크립트는 그 전까지 쓰는 임시 대응이다.

ALTER TABLE checkpoints MODIFY COLUMN guide_content TEXT;
ALTER TABLE courses MODIFY COLUMN description TEXT;
