package com.tourismdata.contest.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 에러 코드 enum
@Getter
@AllArgsConstructor
public enum ErrorCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "존재하지 않는 코스입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
