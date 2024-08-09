package com.team2.finalproject.domain.vehicledetail.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;

public class VehicleDetailException extends HttpStatusCodeException {

    public VehicleDetailException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
