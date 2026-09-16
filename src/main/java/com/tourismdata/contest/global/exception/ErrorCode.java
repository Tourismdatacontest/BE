package com.tourismdata.contest.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 에러 코드 enum
@Getter
@AllArgsConstructor
public enum ErrorCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "존재하지 않는 코스입니다."),
    MODE_NOT_FOUND(HttpStatus.NOT_FOUND, "MODE_NOT_FOUND", "존재하지 않는 모드입니다."),
    VISIT_NOT_FOUND(HttpStatus.NOT_FOUND, "VISIT_NOT_FOUND", "존재하지 않는 탐방 세션입니다."),
    CHECKPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKPOINT_NOT_FOUND", "존재하지 않는 체크포인트입니다."),
    INVALID_VISIT_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_VISIT_REQUEST", "존재하지 않는 코스 또는 모드입니다."),

    // ⚠️ 아래는 Story/Auth 도메인(정현) 추가분 - 병합 전 A 확인 필요.
    STORY_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STORY_EVENT_NOT_FOUND", "존재하지 않는 스토리 이벤트입니다."),
    INGREDIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "INGREDIENT_NOT_FOUND", "존재하지 않는 재료입니다."),
    KAKAO_LOGIN_FAILED(HttpStatus.BAD_GATEWAY, "KAKAO_LOGIN_FAILED", "카카오 로그인 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
