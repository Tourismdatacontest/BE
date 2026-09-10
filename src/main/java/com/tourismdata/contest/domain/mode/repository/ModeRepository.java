package com.tourismdata.contest.domain.mode.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tourismdata.contest.domain.mode.entity.Mode;

public interface ModeRepository extends JpaRepository<Mode, Long> {

    Optional<Mode> findByName(String name);

    // name에 걸린 유니크 제약 기반 원자적 upsert. 동시에 시딩 요청이 들어와도
    // findByName -> save 방식과 달리 DB 레벨에서 경쟁 없이 하나로 정리된다.
    // 이미 존재하면 아무것도 바꾸지 않는다(mode_id를 자기 자신으로 갱신하는 no-op).
    //
    // ⚠️ ddl-auto: update는 "이미 존재하는 테이블/컬럼"에 유니크 제약을 소급 적용하지
    // 못하는 경우가 많다(Hibernate의 알려진 한계 - CREATE TABLE 시점엔 반영되지만
    // ALTER TABLE로는 잘 안 붙는다). 이 변경 전에 이미 로컬 modes 테이블이 있던
    // 환경이라면 수동으로 한 번 추가해야 한다:
    //   ALTER TABLE modes ADD UNIQUE INDEX uk_modes_name (name);
    // (완전히 새로 생성되는 테이블/환경이라면 CREATE TABLE에 자동으로 포함되어 문제 없음)
    @Modifying
    @Query(value = "INSERT INTO modes (name, description) VALUES (:name, :description) "
            + "ON DUPLICATE KEY UPDATE mode_id = mode_id", nativeQuery = true)
    void upsertIfAbsent(@Param("name") String name, @Param("description") String description);
}
