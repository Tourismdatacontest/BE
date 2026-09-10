package com.tourismdata.contest.domain.mode.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourismdata.contest.domain.mode.entity.Mode;

public interface ModeRepository extends JpaRepository<Mode, Long> {

    Optional<Mode> findByName(String name);
}
