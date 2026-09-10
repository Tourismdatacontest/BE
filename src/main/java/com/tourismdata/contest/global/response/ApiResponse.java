package com.tourismdata.contest.global.response;

import com.tourismdata.contest.global.exception.ErrorCode;

// 공통 API 응답 래퍼.
// 성공 응답은 각 컨트롤러가 명세(OpenAPI)에 정의된 DTO를 그대로 반환하고,
// 이 래퍼는 GlobalExceptionHandler가 만드는 에러 응답 바디 형식으로 사용한다.
public record ApiResponse<T>(boolean success, T data, String code, String message) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getCode(), errorCode.getMessage());
    }
}
