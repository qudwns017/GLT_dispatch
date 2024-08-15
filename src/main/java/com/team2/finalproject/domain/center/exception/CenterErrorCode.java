package com.team2.finalproject.domain.center.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CenterErrorCode implements ErrorCode {
    NOT_FOUND_CENTER(HttpStatus.NOT_FOUND, "존재하지 않는 센터입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
