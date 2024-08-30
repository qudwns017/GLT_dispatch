package com.team2.finalproject.domain.dispatchdetail.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DispatchDetailErrorCode implements ErrorCode {
    INVALID_IN_REQUEST(HttpStatus.BAD_REQUEST,"올바르지 않은 배차상세 id가 있습니다."),
    NOT_FOUND_TRANSPORT_ORDER_IN_DISPATCH_DETAIL(HttpStatus.NOT_FOUND,"배차상세에 맞는 운송실행주문이 없습니다."),
    NOT_MATCH_CENTER_AND_DISPATCH_DETAIL(HttpStatus.BAD_REQUEST,"해당 센터의 주문이 아닌 데이터가 존재합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
