package com.team2.finalproject.domain.transportorder.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class TransportOrderException extends HttpStatusCodeException {

    public TransportOrderException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
