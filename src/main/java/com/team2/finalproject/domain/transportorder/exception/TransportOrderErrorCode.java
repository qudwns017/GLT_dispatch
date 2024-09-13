package com.team2.finalproject.domain.transportorder.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TransportOrderErrorCode implements ErrorCode {
    NOT_FOUND_TRANSPORT_ORDER(HttpStatus.NOT_FOUND, "존재하지 않는 운송실행주문입니다."),
    NOT_FOUND_ADDRESS(HttpStatus.NOT_FOUND, "존재하지 않는 주소입니다."),
    FAILED_OPTIMIZE_ROUTE(HttpStatus.BAD_REQUEST, "경로 최적화에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
