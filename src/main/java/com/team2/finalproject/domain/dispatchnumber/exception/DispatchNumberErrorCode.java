package com.team2.finalproject.domain.dispatchnumber.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DispatchNumberErrorCode implements ErrorCode {
    NOT_FOUND_DISPATCH_NUMBER(HttpStatus.NOT_FOUND,"배차코드를 찾지 못했습니다."),
    INVALID_IN_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 배차번호 id가 있습니다."),
    CANNOT_CANCEL_COMPLETED_DISPATCH_NUMBER(HttpStatus.BAD_REQUEST, "완료된 배차는 삭제할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
