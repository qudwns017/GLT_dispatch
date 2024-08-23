package com.team2.finalproject.domain.deliverydestination.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryDestinationErrorCode implements ErrorCode {
    NOT_FOUND_DELIVERY_DESTINATION_ID(HttpStatus.NOT_FOUND, "존재하지 않는 배송처입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
