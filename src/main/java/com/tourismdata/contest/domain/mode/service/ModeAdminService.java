package com.tourismdata.contest.domain.mode.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourismdata.contest.domain.mode.dto.ModeSummaryResponse;
import com.tourismdata.contest.domain.mode.entity.Mode;
import com.tourismdata.contest.domain.mode.repository.ModeRepository;

import lombok.RequiredArgsConstructor;

// 기획 확정(일반 모드 / 스토리 모드 2종)에 따른 기본 Mode 데이터 시딩 도구.
// Course와 달리 Mode는 처음부터 실제 서비스 데이터라 교체할 placeholder가 없으므로,
// 전체 삭제 후 재생성하는 대신 이름 기준으로 있으면 건너뛰고 없으면 생성하는
// 멱등한 upsert 방식을 쓴다 (Visit이 mode_id를 FK로 참조하므로 기존 데이터를
// 함부로 지우지 않기 위함).
//
// CodeRabbit 지적 반영: findByName().orElseGet(save)는 원자적이지 않아 동시 요청 시
// 중복 생성되거나(유니크 제약 없으면) duplicate-key로 실패할 수 있었다(유니크 제약 있으면
// 복구 로직 부재). name에 유니크 제약을 걸고, ModeRepository.upsertIfAbsent()로
// DB 레벨 INSERT ... ON DUPLICATE KEY UPDATE를 써서 경쟁 자체를 없앴다.
@Service
@RequiredArgsConstructor
@Transactional
public class ModeAdminService {

    private static final String GENERAL_MODE_NAME = "일반 모드";
    private static final String GENERAL_MODE_DESCRIPTION =
            "체크포인트에 도착하면 장소별 핵심 역사 정보를 짧게 확인하는 모드. 도깨비 서사나 재료 수집 없이 가볍게 둘러보고 싶을 때 선택한다.";
    private static final String STORY_MODE_NAME = "스토리 모드";
    private static final String STORY_MODE_DESCRIPTION =
            "병자호란 47일 항전을 도깨비와 함께 따라가는 모드. 체크포인트에 도착하면 도깨비의 이야기와 퀘스트가 이어지고, 백숙 재료를 하나씩 모아 도깨비의 잃어버린 기억을 되찾는다.";

    private final ModeRepository modeRepository;

    public List<ModeSummaryResponse> seedDefaultModes() {
        Mode general = upsert(GENERAL_MODE_NAME, GENERAL_MODE_DESCRIPTION);
        Mode story = upsert(STORY_MODE_NAME, STORY_MODE_DESCRIPTION);
        return List.of(ModeSummaryResponse.from(general), ModeSummaryResponse.from(story));
    }

    private Mode upsert(String name, String description) {
        modeRepository.upsertIfAbsent(name, description);
        return modeRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Mode upsert 직후 조회 실패: name=" + name));
    }
}
