package com.team2.finalproject.domain.vehicle.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VehicleErrorCode implements ErrorCode {

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
