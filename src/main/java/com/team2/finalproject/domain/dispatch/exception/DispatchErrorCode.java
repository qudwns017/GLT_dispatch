package com.team2.finalproject.domain.dispatch.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DispatchErrorCode implements ErrorCode {
    WRONG_SEARCH_OPTION(HttpStatus.BAD_REQUEST, "올바르지 않은 검색 옵션 입니다."),
    NOT_FOUND_DISPATCH(HttpStatus.NOT_FOUND, "존재하지 않는 배차입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
