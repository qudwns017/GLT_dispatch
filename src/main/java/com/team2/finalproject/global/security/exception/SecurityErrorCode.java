package com.team2.finalproject.global.security.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "블랙리스트에 존재하는 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레쉬 토큰을 찾을 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
