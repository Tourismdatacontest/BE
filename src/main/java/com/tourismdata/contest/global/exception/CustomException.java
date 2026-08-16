package com.tourismdata.contest.global.exception;

import lombok.Getter;

// 공통 커스텀 예외
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
