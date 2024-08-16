package com.team2.finalproject.domain.dispatch.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DispatchErrorCode implements ErrorCode {
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
