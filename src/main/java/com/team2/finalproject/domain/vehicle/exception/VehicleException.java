package com.team2.finalproject.domain.vehicle.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;

public class VehicleException  extends HttpStatusCodeException {

    public VehicleException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
