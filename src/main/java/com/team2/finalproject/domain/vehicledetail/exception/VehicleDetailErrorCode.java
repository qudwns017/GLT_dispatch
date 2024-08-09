package com.team2.finalproject.domain.vehicledetail.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum VehicleDetailErrorCode implements ErrorCode {

    ;

    private String message;
    private HttpStatus httpStatus;
}
